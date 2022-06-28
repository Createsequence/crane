package top.xiajibagao.crane.core.handler;

import cn.hutool.core.collection.CollUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.xiajibagao.crane.core.annotation.GroupRegister;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandler;
import top.xiajibagao.crane.core.operator.interfaces.GroupRegistrable;
import top.xiajibagao.crane.core.operator.interfaces.OperateProcessor;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.Operation;
import top.xiajibagao.crane.core.parser.interfaces.PropertyMapping;

import java.util.Map;

/**
 * 处理Map类型的数据源与待处理对象
 *
 * @author huangchengxing
 * @date 2022/04/08 9:54
 * @since 0.2.0
 */
@GroupRegister(GroupRegistrable.OPERATE_GROUP_JAVA_BEAN)
@Getter
@RequiredArgsConstructor
public class MapOperateHandler implements OperateHandler {

    private final OperateProcessor operateProcessor;

    @Override
    public boolean sourceCanRead(Object source, PropertyMapping property, Operation operation) {
        return source instanceof Map;
    }

    @Override
    public Object readFromSource(Object source, PropertyMapping property, Operation operation) {
        Map<String, Object> sourceMap = parseMap(source);
        if (CollUtil.isEmpty(sourceMap)) {
            return null;
        }
        return property.hasResource() ?
            sourceMap.get(property.getSource()) : sourceMap;
    }

    @Override
    public boolean targetCanWrite(Object sourceData, Object target, PropertyMapping property, AssembleOperation operation) {
        return target instanceof Map;
    }

    @Override
    public void writeToTarget(Object sourceData, Object target, PropertyMapping property, AssembleOperation operation) {
        Map<String, Object> targetMap = parseMap(target);
        if (CollUtil.isEmpty(targetMap)) {
            return;
        }

        // 不存在引用字段时，尝试将数据添加到key字段对应的位置
        if (!property.hasReference()) {
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
