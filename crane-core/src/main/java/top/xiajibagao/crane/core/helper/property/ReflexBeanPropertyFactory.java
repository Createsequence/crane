package top.xiajibagao.crane.core.helper.property;

import cn.hutool.core.lang.Assert;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import top.xiajibagao.crane.core.helper.ObjectUtils;
import top.xiajibagao.crane.core.helper.invoker.MethodInvoker;
import top.xiajibagao.crane.core.helper.invoker.ParamTypeAutoConvertInvoker;
import top.xiajibagao.crane.core.helper.invoker.ReflexMethodInvoker;
import top.xiajibagao.crane.core.helper.reflex.ReflexUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 用于生产基于java原生的反射调用实现的{@link BeanProperty}
 *
 * @author huangchengxing
 * @date 2022/06/02 8:25
 * @since 0.5.4
 */
public class ReflexBeanPropertyFactory extends AbstractBeanPropertyFactory implements BeanPropertyFactory {

    @Override
    protected BeanProperty createBeanProperty(Class<?> targetClass, Field field) {
        Method getter = ReflexUtils.findGetterMethod(targetClass, field);
        Assert.notNull(getter, "属性{}找不到对应的Getter方法", field);
        Method setter = ReflexUtils.findSetterMethod(targetClass, field);
        Assert.notNull(setter, "属性{}找不到对应的Setter方法", field);
        return new ReflexBeanPropertyFactory.ReflexBeanProperty(
            targetClass, field,
            new ParamTypeAutoConvertInvoker(getter.getParameterTypes(), new ReflexMethodInvoker(getter)),
            new ParamTypeAutoConvertInvoker(setter.getParameterTypes(), new ReflexMethodInvoker(setter))
        );
    }

    /**
     * 基于java原生的反射调用实现的{@link BeanProperty}
     *
     * @author huangchengxing
     * @date 2022/05/11 11:43
     */
    @Accessors(fluent = true)
    @RequiredArgsConstructor
    public static class ReflexBeanProperty implements BeanProperty {
        @Getter
        private final Class<?> targetClass;
        @Getter
        private final Field field;
        private final MethodInvoker getter;
        private final MethodInvoker setter;

        @Override
        public Object getValue(Object target) {
            return ObjectUtils.computeIfNotNull(target, getter::invoke);
        }

        @Override
        public void setValue(Object target, Object value) {
            ObjectUtils.computeIfNotNull(target, t -> setter.invoke(t, value));
        }

    }
}
