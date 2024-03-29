package io.github.createsequence.crane.core.handler;

import io.github.createsequence.crane.core.annotation.ProcessorComponent;
import io.github.createsequence.crane.core.handler.interfaces.OperateHandler;
import io.github.createsequence.crane.core.helper.property.BeanPropertyFactory;
import io.github.createsequence.crane.core.operator.interfaces.OperateProcessor;
import io.github.createsequence.crane.core.operator.interfaces.OperateProcessorComponent;
import io.github.createsequence.crane.core.parser.interfaces.AssembleOperation;
import io.github.createsequence.crane.core.parser.interfaces.Operation;
import io.github.createsequence.crane.core.parser.interfaces.PropertyMapping;

import java.util.Objects;

/**
 * 处理对象类型数据源与待处理对象，也是用于兜底的处理器
 *
 * @author huangchengxing
 * @date 2022/04/08 16:44
 * @since 0.2.0
 */
@ProcessorComponent(OperateProcessorComponent.OPERATE_GROUP_JAVA_BEAN)
public class BeanOperateHandler extends AbstractOperateHandler implements OperateHandler {

    private final BeanPropertyFactory beanPropertyFactory;

    public BeanOperateHandler(OperateProcessor operateProcessor, BeanPropertyFactory beanPropertyFactory, String... defaultRegisterGroups) {
        super(operateProcessor, defaultRegisterGroups);
        this.beanPropertyFactory = beanPropertyFactory;
    }

    @Override
    public boolean sourceCanRead(Object source, PropertyMapping property, Operation operation) {
        return Objects.nonNull(source);
    }

    @Override
    public Object readFromSource(Object source, PropertyMapping property, Operation operation) {
        // 若指定数据源字段，则尝试从数据源上获取数据
        if (property.hasResource()) {
            return beanPropertyFactory.getProperty(source.getClass(), property.getSource())
                .map(bp -> bp.getValue(source))
                .orElse(null);
        }
        return source;
    }

    @Override
    public boolean targetCanWrite(Object sourceData, Object target, PropertyMapping property, AssembleOperation operation) {
        return Objects.nonNull(target);
    }

    @Override
    public void writeToTarget(Object sourceData, Object target, PropertyMapping property, AssembleOperation operation) {
        String operateProperty = property.hasReference() ?
            property.getReference() : operation.getTargetProperty().getName();
        beanPropertyFactory.getProperty(target.getClass(), operateProperty)
            .ifPresent(bp -> bp.setValue(target, sourceData));
    }

}
