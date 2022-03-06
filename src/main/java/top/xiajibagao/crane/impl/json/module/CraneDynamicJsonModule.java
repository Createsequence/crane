package top.xiajibagao.crane.impl.json.module;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import top.xiajibagao.crane.helper.CacheableAnnotationProcessor;

import java.io.IOException;

/**
 * @author huangchengxing
 * @date 2022/03/06 16:41
 */
public class CraneDynamicJsonModule extends Module {

    private final CacheableAnnotationProcessor<Class<?>> processor;
    private final ObjectMapper objectMapper;

    public CraneDynamicJsonModule(ObjectMapper objectMapper, CacheableAnnotationProcessor<Class<?>> processor) {
        this.objectMapper = objectMapper;
        this.processor = processor;
    }

    public CraneDynamicJsonModule(ObjectMapper objectMapper, BeanFactory beanFactory) {
        this.objectMapper = objectMapper;
        this.processor = new CacheableAnnotationProcessor.SimpleCacheableAnnotationProcessor<>(beanFactory, Class::getName);
    }

    @Override
    public String getModuleName() {
        return getClass().getName();
    }

    @Override
    public Version version() {
        return Version.unknownVersion();
    }

    @Override
    public void setupModule(SetupContext context) {
        context.addBeanSerializerModifier(new BeanSerializerModifier() {
            @Override
            public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
                return AnnotatedElementUtils.hasAnnotation(beanDesc.getBeanClass(), ProcessJson.class) ?
                    new DynamicJsonPropertySerializer<>(beanDesc.getBeanClass(), objectMapper, processor) : serializer;
            }
        });
    }

    /**
     * 用于填充字典字段的序列化器，序列化时将动态将字典的value字段添加到json对象中 <br />
     * 注意：<b>传入实例的{@link ObjectMapper}不能与该序列化器注入的实例相同，否则将进入死循环</b>
     *
     * @author huangchengxing
     * @date 2022/01/05 18:08
     */
    public static class DynamicJsonPropertySerializer<T> extends StdSerializer<T> {

        private final ObjectMapper objectMapper;
        private final CacheableAnnotationProcessor<Class<?>> processor;

        public DynamicJsonPropertySerializer(Class<T> t, ObjectMapper objectMapper, CacheableAnnotationProcessor<Class<?>> processor) {
            super(t);
            this.objectMapper = objectMapper;
            this.processor = processor;
        }

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            if (value == null) {
                return;
            }
            JsonNode jsonNode = objectMapper.valueToTree(value);
            processor.process(handledType(), jsonNode);
            objectMapper.writeTree(gen, jsonNode);
        }

    }
}
