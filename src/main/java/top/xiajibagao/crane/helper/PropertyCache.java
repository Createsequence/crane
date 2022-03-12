package top.xiajibagao.crane.helper;

import lombok.Getter;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import top.xiajibagao.crane.exception.CraneException;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 属性描述器缓存
 *
 * @author huangchengxing
 * @date 2022/3/2 14:07
 */
public class PropertyCache {
    @Getter
    private final PropertyDescriptor descriptor;
    @Getter
    private final Class<?> targetClass;
    private final Method getter;
    private final Method setter;

    public PropertyCache(PropertyDescriptor descriptor, Class<?> targetClass) {
        this.descriptor = descriptor;
        this.targetClass = targetClass;

        this.getter = ObjectUtils.defaultIfNull(
            descriptor.getReadMethod(),
            BeanPropertyUtils.findGetterMethod(targetClass, descriptor.getName())
        );
        Objects.requireNonNull(this.getter, String.format("[%s]属性[%s]没有getter方法！", targetClass, descriptor.getName()));

        this.setter = ObjectUtils.defaultIfNull(
            descriptor.getWriteMethod(),
            BeanPropertyUtils.findSetterMethod(targetClass, descriptor.getName(), descriptor.getPropertyType())
        );
        Objects.requireNonNull(this.setter, String.format("[%s]属性[%s]没有setter方法！", targetClass, descriptor.getName()));
    }

    public String getName() {
        return descriptor.getName();
    }

    public void setValue(Object target, Object value) {
        CraneException.throwIfFalse(
            ClassUtils.isAssignable(descriptor.getPropertyType(), value.getClass()),
            "[%s]字段[%s]类型为[%s]，但待映射的值[%s]类型为[%s]",
            targetClass, descriptor.getName(), descriptor.getPropertyType(),
            value, value.getClass()
        );
        ReflectionUtils.invokeMethod(setter, target, value);
    }

    public Object getValue(Object target) {
        return ReflectionUtils.invokeMethod(getter, target);
    }

}
