package top.xiajibagao.crane.helper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author huangchengxing
 * @date 2022/04/01 14:08
 */
@Accessors(fluent = true)
@RequiredArgsConstructor
@Getter
public class PropertyCache {
    private final Class<?> targetClass;
    private final Field field;
    private final Method getter;
    private final Method setter;

    public Object getValue(Object target) {
        return ObjectUtils.computeIfNotNull(target, t -> ReflectionUtils.invokeMethod(getter, t));
    }

    public void setValue(Object target, Object value) {
        ReflectionUtils.invokeMethod(setter, target, value);
    }

}
