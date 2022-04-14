package top.xiajibagao.crane.core.handler;

import top.xiajibagao.crane.core.helper.ReflexUtils;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.AssembleProperty;

/**
 * 处理对象类型数据源与待处理对象，也是用于兜底的处理器
 *
 * @author huangchengxing
 * @date 2022/04/08 16:44
 */
public class BeanOperateHandler implements OperateHandler {

    @Override
    public boolean sourceCanRead(Object source, AssembleProperty property, AssembleOperation operation) {
        return true;
    }

    @Override
    public boolean targetCanWrite(Object sourceData, Object target, AssembleProperty property, AssembleOperation operation) {
        return true;
    }

    @Override
    public Object readFromSource(Object source, AssembleProperty property, AssembleOperation operation) {
        // 若指定数据源字段，则尝试从数据源上获取数据
        if (property.hasResource()) {
            return ReflexUtils.findProperty(source.getClass(), property.getResource())
                .map(p -> p.getValue(source))
                .orElse(null);
        }
        return source;
    }

    @Override
    public void writeToTarget(Object sourceData, Object target, AssembleProperty property, AssembleOperation operation) {
        String operateProperty = property.hasReference() ?
            property.getReference() : operation.getTargetProperty().getName();
        ReflexUtils.findProperty(target.getClass(), operateProperty)
            .ifPresent(p -> p.setValue(target, sourceData));
    }

}
