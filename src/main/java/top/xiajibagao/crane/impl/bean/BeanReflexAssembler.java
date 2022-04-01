package top.xiajibagao.crane.impl.bean;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import top.xiajibagao.crane.exception.CraneException;
import top.xiajibagao.crane.helper.PropertyCache;
import top.xiajibagao.crane.helper.PropertyUtils;
import top.xiajibagao.crane.operator.interfaces.Assembler;
import top.xiajibagao.crane.parse.interfaces.AssembleOperation;
import top.xiajibagao.crane.parse.interfaces.AssembleProperty;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author huangchengxing
 * @date 2022/03/02 13:29
 */
@Getter
@RequiredArgsConstructor
public class BeanReflexAssembler implements Assembler {

    @Override
    public void execute(Object target, Object source, AssembleOperation operation) {
        if (Objects.isNull(target) || Objects.isNull(source)) {
            return;
        }
        checkType(target, operation);

        Class<?> targetClass = target.getClass();
        if (CollectionUtils.isEmpty(operation.getProperties())) {
            processIfNonProperties(targetClass, target, source, operation);
            return;
        }

        // TODO 此处字段处理策略应当可以自定义，或改为从特定容器中获取
        Class<?> sourceClass = source.getClass();
        operation.getProperties().forEach(p -> {
            if (sourceClass.isAssignableFrom(Collection.class)) {
                processIfCollectionSource(targetClass, target, (Collection<?>)source, p, operation);
            } else if (sourceClass.isArray()) {
                processIfArraySource(targetClass, target, (Object[])source, p, operation);
            } else {
                processIfObjectSource(targetClass, target, sourceClass, source, p, operation);
            }
        });
    }

    /**
     * 若未配置任何字段，则直接使用数据源填充注解字段
     *
     * @param targetClass 目标类型
     * @param target 目标实例
     * @param source 受教育
     * @param operation 操作配置
     * @author huangchengxing
     * @date 2022/3/2 16:01
     */
    protected void processIfNonProperties(Class<?> targetClass, Object target, Object source, AssembleOperation operation) {
        PropertyUtils.getPropertyCache(targetClass, operation.getTargetProperty().getName())
            .ifPresent(c -> c.setValue(target, source));
    }

    /**
     * 若数据源类型为对象或Map集合：
     * 1.若数据源存在引用字段，则将数据源对应字段的值填充至目标实例的指定属性中；
     * 2.若数据源不存在引用字段，则将数据源对象填充至目标实例指定属性中；
     *
     * @param targetClass 目标类型
     * @param target 目标实例
     * @param sourceClass 数据源类型
     * @param source 数据源
     * @param property 字段配置
     * @param operation 操作配置
     * @author huangchengxing
     * @date 2022/3/2 16:01
     */
    protected void processIfObjectSource(Class<?> targetClass, Object target, Class<?> sourceClass, Object source, AssembleProperty property, AssembleOperation operation) {
        Optional<PropertyCache> targetProperty = PropertyUtils.getPropertyCache(targetClass, property.getReference());
        if (!targetProperty.isPresent()) {
            return;
        }

        // 未设置引用字段
        if (!StringUtils.hasText(property.getResource())) {
            targetProperty.get().setValue(target, source);
            return;
        }

        // 若设置了引用字段，且数据源为Map集合
        if (source instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> sourceMap = (Map<String, Object>) source;
            Object val = sourceMap.get(property.getResource());
            if (Objects.nonNull(val)) {
                targetProperty.ifPresent(t -> t.setValue(target, val));
            }
            return;
        }

        // 若设置了引用字段，且数据源为对象
        PropertyUtils.getPropertyCache(sourceClass, property.getResource())
            .ifPresent(sourceProperty -> targetProperty.get().setValue(
                target, sourceProperty.getValue(source)
            ));
    }

    /**
     * 若数据源类型为集合，则将数组值填充至目标实例的指定属性中
     *
     * @param targetClass 目标类型
     * @param target 目标实例
     * @param source 数据源
     * @param property 字段配置
     * @param operation 操作配置
     * @author huangchengxing
     * @date 2022/3/2 16:01
     */
    protected void processIfCollectionSource(Class<?> targetClass, Object target, Collection<?> source, AssembleProperty property, AssembleOperation operation) {
        PropertyUtils.getPropertyCache(targetClass, property.getReference())
            .ifPresent(c -> c.setValue(target, source));
    }
    
    /**
     * 若数据源类型为数组，则将数组值填充至目标实例的指定属性中
     *
     * @param targetClass 目标类型
     * @param target 目标实例
     * @param source 受教育
     * @param property 字段配置
     * @param operation 操作配置
     * @author huangchengxing
     * @date 2022/3/2 16:01
     */
    protected void processIfArraySource(Class<?> targetClass, Object target, Object[] source, AssembleProperty property, AssembleOperation operation) {
        PropertyUtils.getPropertyCache(targetClass, property.getReference())
            .ifPresent(c -> c.setValue(target, source));
    }

    @Override
    public Object getKey(Object target, AssembleOperation operation) {
        checkType(target, operation);
        return PropertyUtils.getPropertyCache(operation.getOwner().getTargetClass(), operation.getTargetProperty().getName())
            .map(dc -> dc.getValue(target))
            .orElse(null);
    }
    
    /**
     * 检查当前正在处理的目标实例是否与操作配置中指定的类型
     *
     * @param target 目标实例
     * @param operation 操作配置
     * @author huangchengxing
     * @date 2022/3/2 16:06
     */
    private void checkType(Object target, AssembleOperation operation) {
        CraneException.throwIfFalse(
            operation.getOwner().getTargetClass().isAssignableFrom(target.getClass()),
            "操作配置类型为[%s]，但待处理数据类型为[%s]",
            operation.getOwner().getTargetClass(), target.getClass()
        );
    }

}
