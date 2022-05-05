package top.xiajibagao.crane.jackson.impl.module;

import cn.hutool.core.lang.Assert;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import top.xiajibagao.crane.core.annotation.ProcessJacksonNode;
import top.xiajibagao.crane.core.executor.OperationExecutor;
import top.xiajibagao.crane.core.operator.interfaces.OperatorFactory;
import top.xiajibagao.crane.core.parser.interfaces.OperateConfigurationParser;
import top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration;

import java.util.Objects;

/**
 * 允许在序列化时基于操作配置动态添加/替换字段与字段值
 *
 * @author huangchengxing
 * @date 2022/04/12 17:58
 */
@RequiredArgsConstructor
public class DynamicJsonNodeModule extends Module {

    private final BeanFactory beanFactory;
    private final ObjectMapper objectMapper;
    private final OperatorFactory defaultOperatorFactory;
    private final OperateConfigurationParser<? extends OperationConfiguration> defaultOperateConfigurationParser;
    private final OperationExecutor defaultOperationExecutor;

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
        context.addBeanSerializerModifier(new DynamicJsonNodeBeanSerializerModifier(
            beanFactory, objectMapper, defaultOperatorFactory, defaultOperateConfigurationParser, defaultOperationExecutor
        ));
    }

    @RequiredArgsConstructor
    private static class DynamicJsonNodeBeanSerializerModifier extends BeanSerializerModifier {

        private final BeanFactory beanFactory;
        private final ObjectMapper objectMapper;
        private final OperatorFactory defaultOperatorFactory;
        private final OperateConfigurationParser<? extends OperationConfiguration> defaultOperateConfigurationParser;
        private final OperationExecutor defaultOperationExecutor;

        @Override
        public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
            ProcessJacksonNode annotation = AnnotatedElementUtils.findMergedAnnotation(beanDesc.getBeanClass(), ProcessJacksonNode.class);
            if (Objects.isNull(annotation)) {
                return serializer;
            }
            OperatorFactory operatorFactory = defaultIfVoid(
                OperatorFactory.class, annotation.operatorFactory(), defaultOperatorFactory
            );
            OperateConfigurationParser<? extends OperationConfiguration> configurationParser = defaultIfVoid(
                OperateConfigurationParser.class, annotation.parser(), defaultOperateConfigurationParser
            );
            OperationConfiguration operationConfiguration = configurationParser.parse(beanDesc.getBeanClass(), operatorFactory);
            OperationExecutor operationExecutor = defaultIfVoid(OperationExecutor.class, annotation.executor(), defaultOperationExecutor);
            return new DynamicJsonNodeBeanSerializer<>(beanDesc.getBeanClass(), objectMapper, operationConfiguration, operationExecutor);
        }

        @SuppressWarnings("unchecked")
        private <T> T defaultIfVoid(Class<T> targetType, Class<?> beanType, T def) {
            if (Objects.equals(Void.class, beanType)) {
                return def;
            }
            Assert.isAssignable(targetType, beanType, "类型[{}]不为[{}]或其子类", targetType, beanType);
            return Objects.equals(Void.class, beanType) ? def : (T) beanFactory.getBean(beanType);
        }

    }

}
