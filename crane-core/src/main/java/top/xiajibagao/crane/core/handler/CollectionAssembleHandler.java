package top.xiajibagao.crane.core.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.util.ClassUtils;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.AssembleProperty;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 处理Collection类型的数据源与待处理对象
 *
 * @author huangchengxing
 * @date 2022/04/08 10:24
 */
@RequiredArgsConstructor
public class CollectionAssembleHandler implements AssembleHandler {

    private final AssembleHandlerChain handlerChain;

    @Override
    public boolean sourceCanRead(Object source, AssembleProperty property, AssembleOperation operation) {
        return ClassUtils.isAssignable(Collection.class, source.getClass());
    }

    @Override
    public boolean targetCanWrite(Object sourceData, Object target, AssembleProperty property, AssembleOperation operation) {
        return ClassUtils.isAssignable(Collection.class, target.getClass());
    }

    @Override
    public Object readFromSource(Object source, AssembleProperty property, AssembleOperation operation) {
        if (Objects.isNull(source)) {
            return null;
        }
        // 若不指定引用字段，则直接返回集合
        if (!property.hasResource()) {
            return source;
        }
        // 若指定引用字段，则尝试从集合中的对象里获取字段
        return parseCollection(source).stream()
            .map(t -> handlerChain.readFromSource(t, property, operation))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Override
    public void writeToTarget(Object sourceData, Object target, AssembleProperty property, AssembleOperation operation) {
        if (Objects.isNull(sourceData) || Objects.isNull(target)) {
            return;
        }
        Collection<Object> targetColl = parseCollection(target);
        targetColl.forEach(t -> handlerChain.writeToTarget(sourceData, t, property, operation));
    }

    @SuppressWarnings("unchecked")
    private Collection<Object> parseCollection(Object data) {
        return (Collection<Object>)data;
    }
}
