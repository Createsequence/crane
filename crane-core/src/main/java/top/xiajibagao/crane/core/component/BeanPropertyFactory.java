package top.xiajibagao.crane.core.component;

import cn.hutool.core.lang.Assert;
import com.esotericsoftware.reflectasm.MethodAccess;
import lombok.RequiredArgsConstructor;
import top.xiajibagao.crane.core.helper.reflex.AsmReflexUtils;
import top.xiajibagao.crane.core.helper.reflex.IndexedMethod;
import top.xiajibagao.crane.core.helper.reflex.ReflexUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;

/**
 * @author huangchengxing
 * @date 2022/05/10 15:51
 */
@RequiredArgsConstructor
public class BeanPropertyFactory {

    private final ConcurrentMap<Class<?>, ConcurrentMap<String, Optional<BeanProperty>>> propertiesCache = new ConcurrentHashMap<>();
    private final BiFunction<Class<?>, Field, BeanProperty> propertyFactory;

    public static final BeanPropertyFactory ASM_REFLEX_PROPERTY_FACTORY = new BeanPropertyFactory((targetClass, field) -> {
        int getterIndex = AsmReflexUtils.findGetterMethodIndex(targetClass, field.getName());
        Assert.isTrue(getterIndex > -1, String.format("属性[%s]找不到对应的Getter方法", field));
        int setterIndex = AsmReflexUtils.findSetterMethodIndex(targetClass, field.getName(), field.getType());
        Assert.isTrue(setterIndex > -1, String.format("属性[%s]找不到对应的Setter方法", field));
        MethodAccess methodAccess = AsmReflexUtils.getMethodAccess(targetClass);
        return new AsmReflexUtils.AsmReflexBeanProperty(
            targetClass, field, new IndexedMethod(methodAccess, getterIndex), new IndexedMethod(methodAccess, setterIndex)
        );
    });

    public static final BeanPropertyFactory REFLEX_PROPERTY_FACTORY = new BeanPropertyFactory((targetClass, field) -> {
        Method getter = ReflexUtils.findGetterMethod(targetClass, field);
        Assert.notNull(getter, "属性{}找不到对应的Getter方法", field);
        Method setter = ReflexUtils.findSetterMethod(targetClass, field);
        Assert.notNull(setter, "属性{}找不到对应的Setter方法", field);
        return new ReflexUtils.ReflexBeanProperty(targetClass, field, getter, setter);
    }
    );

    /**
     * 获取一个BeanProperty
     *
     * @param targetClass 目标类型
     * @param fieldName 字段名
     * @return java.util.Optional<top.xiajibagao.crane.core.component.BeanProperty>
     * @author huangchengxing
     * @date 2022/5/10 16:05
     */
    public Optional<BeanProperty> getProperty(Class<?> targetClass, String fieldName) {
        ConcurrentMap<String, Optional<BeanProperty>> properties = propertiesCache.computeIfAbsent(targetClass, t -> new ConcurrentHashMap<>(16));
        return properties.computeIfAbsent(fieldName, fn -> createProperty(targetClass, fn));
    }

    /**
     * 根据指定方法创建一个BeanProperty
     */
    private Optional<BeanProperty> createProperty(Class<?> targetClass, String fieldName) {
        Field field = ReflexUtils.findField(targetClass, fieldName);
        if (Objects.isNull(field)) {
            return Optional.empty();
        }
        return Optional.ofNullable(propertyFactory.apply(targetClass, field));
    }

}
