package top.xiajibagao.crane.core.handler;

import org.springframework.util.ClassUtils;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandler;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.AssembleProperty;
import top.xiajibagao.crane.core.parser.interfaces.Operation;

import java.util.Map;
import java.util.Objects;

/**
 * 处理Map类型的数据源与待处理对象
 *
 * @since 0.2.0
 * @author huangchengxing
 * @date 2022/04/08 9:54
 */
public class MapOperateHandler implements OperateHandler {

    @Override
    public boolean sourceCanRead(Object source, AssembleProperty property, Operation operation) {
        return ClassUtils.isAssignable(Map.class, source.getClass());
    }

    @Override
    public boolean targetCanWrite(Object sourceData, Object target, AssembleProperty property, AssembleOperation operation) {
        return ClassUtils.isAssignable(Map.class, target.getClass());
    }

    @Override
    public Object readFromSource(Object source, AssembleProperty property, Operation operation) {
        if (Objects.isNull(source)) {
            return null;
        }
        Map<String, Object> sourceMap = parseMap(source);
        return property.hasResource() ?
            sourceMap.get(property.getResource()) : sourceMap;
    }

    @Override
    public void writeToTarget(Object sourceData, Object target, AssembleProperty property, AssembleOperation operation) {
        if (Objects.isNull(sourceData) || Objects.isNull(target)) {
            return;
        }
        Map<String, Object> targetMap = parseMap(target);
        // 不存在引用字段时，尝试将数据添加到key字段对应的位置
        if (property.hasReference()) {
            targetMap.put(operation.getTargetProperty().getName(), sourceData);
            return;
        }
        // 当存在引用字段时，将数据添加到引用字段对应的key下的位置
        targetMap.put(property.getReference(), sourceData);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseMap(Object data) {
        return (Map<String, Object>)data;
    }

}
