package top.xiajibagao.crane.core.helper.reflex;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import top.xiajibagao.crane.core.helper.ObjectUtils;

import java.lang.reflect.Field;

/**
 * @author huangchengxing
 * @date 2022/05/11 11:43
 */
@Accessors(fluent = true)
@RequiredArgsConstructor
public class AsmReflexBeanProperty implements BeanProperty {
    @Getter
    private final Class<?> targetClass;
    @Getter
    private final Field field;
    private final IndexedMethod getter;
    private final IndexedMethod setter;

    @Override
    public Object getValue(Object target) {
        return ObjectUtils.computeIfNotNull(target, getter::invoke);
    }

    @Override
    public void setValue(Object target, Object value) {
        setter.invoke(target, value);
    }

}
