package top.xiajibagao.crane.core.component;

import cn.hutool.core.lang.Assert;
import com.esotericsoftware.reflectasm.MethodAccess;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import top.xiajibagao.crane.core.helper.ObjectUtils;
import top.xiajibagao.crane.core.helper.invoker.AsmReflexMethodInvoker;
import top.xiajibagao.crane.core.helper.invoker.MethodInvoker;
import top.xiajibagao.crane.core.helper.invoker.ParamTypeAutoConvertInvoker;
import top.xiajibagao.crane.core.helper.reflex.AsmReflexUtils;
import top.xiajibagao.crane.core.helper.reflex.ReflexUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 用于生产基于字节码的反射调用实现的{@link BeanProperty}
 *
 * @author huangchengxing
 * @date 2022/06/02 8:18
 * @since 0.5.4
 */
public class AsmReflexBeanPropertyFactory extends AbstractBeanPropertyFactory implements BeanPropertyFactory {

    @Override
    protected BeanProperty createBeanProperty(Class<?> targetClass, Field field) {
        MethodAccess methodAccess = AsmReflexUtils.getMethodAccess(targetClass);

        Method getter = ReflexUtils.findGetterMethod(targetClass, field);
        Assert.notNull(getter, "属性[{}]找不到对应的Getter方法", field);
        int getterIndex = methodAccess.getIndex(getter.getName(), getter.getParameterTypes());
        MethodInvoker getterInvoker = new AsmReflexMethodInvoker(methodAccess, getterIndex);

        Method setter = ReflexUtils.findSetterMethod(targetClass, field);
        Assert.notNull(setter, "属性[{}]找不到对应的Setter方法", field);
        int setterIndex = methodAccess.getIndex(setter.getName(), setter.getParameterTypes());
        MethodInvoker setterInvoker = new AsmReflexMethodInvoker(methodAccess, setterIndex);
        return new AsmReflexBeanProperty(
            targetClass, field, getterInvoker, setterInvoker
        );
    }

    /**
     * 基于字节码的反射调用实现的{@link BeanProperty}
     *
     * @author huangchengxing
     * @date 2022/05/11 11:43
     */
    @Accessors(fluent = true)
    @RequiredArgsConstructor
    public static class AsmReflexBeanProperty implements BeanProperty {
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
