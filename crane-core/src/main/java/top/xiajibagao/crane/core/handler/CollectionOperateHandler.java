package top.xiajibagao.crane.core.handler;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandler;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandlerChain;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.Operation;
import top.xiajibagao.crane.core.parser.interfaces.PropertyMapping;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 处理Collection类型的数据源与待处理对象
 *
 * @author huangchengxing
 * @date 2022/04/08 10:24
 * @since 0.2.0
 */
@RequiredArgsConstructor
public class CollectionOperateHandler implements OperateHandler {

    private final OperateHandlerChain handlerChain;

    @Override
    public boolean sourceCanRead(Object source, PropertyMapping property, Operation operation) {
        return source instanceof Collection;
    }

    @Override
    public Object readFromSource(Object source, PropertyMapping property, Operation operation) {
        Collection<?> sourceColl = parseCollection(source);
        if (CollUtil.isEmpty(sourceColl)) {
            return null;
        }
        // 若不指定引用字段，则直接返回集合
        if (!property.hasResource()) {
            return source;
        }
        // 若指定引用字段，则尝试从集合中的对象里获取字段
        return sourceColl.stream()
            .map(t -> handlerChain.readFromSource(t, property, operation))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Override
    public boolean targetCanWrite(Object sourceData, Object target, PropertyMapping property, AssembleOperation operation) {
        return target instanceof Collection;
    }

    @Override
    public void writeToTarget(Object sourceData, Object target, PropertyMapping property, AssembleOperation operation) {
        Collection<Object> targetColl = parseCollection(target);
        if (CollUtil.isEmpty(targetColl)) {
            return;
        }
        targetColl.forEach(t -> handlerChain.writeToTarget(sourceData, t, property, operation));
    }

    @SuppressWarnings("unchecked")
    private Collection<Object> parseCollection(Object data) {
        return (Collection<Object>)data;
    }
}
