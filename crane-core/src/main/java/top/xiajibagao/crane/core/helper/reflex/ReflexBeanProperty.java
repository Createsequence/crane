package top.xiajibagao.crane.core.helper.reflex;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.util.ReflectionUtils;
import top.xiajibagao.crane.core.helper.ObjectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author huangchengxing
 * @date 2022/05/11 11:43
 */
@Accessors(fluent = true)
@RequiredArgsConstructor
public class ReflexBeanProperty implements BeanProperty {
    @Getter
    private final Class<?> targetClass;
    @Getter
    private final Field field;
    private final Method getter;
    private final Method setter;

    @Override
    public Object getValue(Object target) {
        return ObjectUtils.computeIfNotNull(target, t -> ReflectionUtils.invokeMethod(getter, t));
    }

    @Override
    public void setValue(Object target, Object value) {
        ReflectionUtils.invokeMethod(setter, target, value);
    }

}
