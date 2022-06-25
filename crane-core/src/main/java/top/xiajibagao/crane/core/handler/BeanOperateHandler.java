package top.xiajibagao.crane.core.handler;

import lombok.RequiredArgsConstructor;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandler;
import top.xiajibagao.crane.core.helper.property.BeanPropertyFactory;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.Operation;
import top.xiajibagao.crane.core.parser.interfaces.PropertyMapping;

import java.util.Objects;

/**
 * 处理对象类型数据源与待处理对象，也是用于兜底的处理器
 *
 * @author huangchengxing
 * @date 2022/04/08 16:44
 * @since 0.2.0
 */
@RequiredArgsConstructor
public class BeanOperateHandler implements OperateHandler {

    private final BeanPropertyFactory beanPropertyFactory;

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
