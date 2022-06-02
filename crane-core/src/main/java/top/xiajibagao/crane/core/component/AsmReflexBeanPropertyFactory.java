package top.xiajibagao.crane.core.component;

import cn.hutool.core.lang.Assert;
import com.esotericsoftware.reflectasm.MethodAccess;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import top.xiajibagao.crane.core.helper.ObjectUtils;
import top.xiajibagao.crane.core.helper.reflex.AsmReflexUtils;
import top.xiajibagao.crane.core.helper.reflex.IndexedMethod;

import java.lang.reflect.Field;

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
        int getterIndex = AsmReflexUtils.findGetterMethodIndex(targetClass, field.getName());
        Assert.isTrue(getterIndex > -1, String.format("属性[%s]找不到对应的Getter方法", field));
        int setterIndex = AsmReflexUtils.findSetterMethodIndex(targetClass, field.getName(), field.getType());
        Assert.isTrue(setterIndex > -1, String.format("属性[%s]找不到对应的Setter方法", field));
        MethodAccess methodAccess = AsmReflexUtils.getMethodAccess(targetClass);
        return new AsmReflexBeanProperty(
            targetClass, field, new IndexedMethod(methodAccess, getterIndex), new IndexedMethod(methodAccess, setterIndex)
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
}
