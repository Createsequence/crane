package io.github.createsequence.crane.jackson.impl.module;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import io.github.createsequence.crane.core.executor.OperationExecutor;
import io.github.createsequence.crane.core.helper.BeanFactoryUtils;
import io.github.createsequence.crane.core.parser.interfaces.OperateConfigurationParser;
import io.github.createsequence.crane.core.parser.interfaces.OperationConfiguration;
import io.github.createsequence.crane.jackson.impl.annotation.ProcessJacksonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.util.Collections;
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
        Assert.isFalse(
            objectMapper.getRegisteredModuleIds().contains(getModuleName()),
            "ObjectMapper实例{}已经注册DynamicJsonNodeModule模块，该实例无法再注册至模块内部"
        );
        context.addBeanSerializerModifier(new DynamicJsonNodeBeanSerializerModifier(beanFactory, objectMapper));
    }

    @RequiredArgsConstructor
    private static class DynamicJsonNodeBeanSerializerModifier extends BeanSerializerModifier {

        private final BeanFactory beanFactory;
        private final ObjectMapper objectMapper;

        // TODO 统一使用ConfigOptionAnnotationProcessor解析
        @Override
        public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
            ProcessJacksonNode annotation = AnnotatedElementUtils.findMergedAnnotation(beanDesc.getBeanClass(), ProcessJacksonNode.class);
            if (Objects.isNull(annotation)) {
                return serializer;
            }
            OperateConfigurationParser configurationParser = BeanFactoryUtils.getBean(beanFactory, annotation.parser(), annotation.parserName());
            OperationConfiguration operationConfiguration = configurationParser.parse(beanDesc.getBeanClass());
            OperationExecutor operationExecutor = BeanFactoryUtils.getBean(beanFactory, annotation.executor(), annotation.executorName());
            return new DynamicJsonNodeBeanSerializer<>(
                beanDesc.getBeanClass(), objectMapper,
                ArrayUtil.isNotEmpty(annotation.groups()) ? CollUtil.newHashSet(annotation.groups()) : Collections.emptySet(),
                operationConfiguration, operationExecutor
            );
        }

    }

}
