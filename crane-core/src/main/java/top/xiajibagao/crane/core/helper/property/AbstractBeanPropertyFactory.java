package top.xiajibagao.crane.core.helper.property;

import top.xiajibagao.crane.core.helper.reflex.ReflexUtils;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * {@link BeanPropertyFactory}抽象类，提供基本逻辑封装与缓存功能
 *
 * @author huangchengxing
 * @date 2022/06/02 8:12
 * @since 0.5.4
 */
public abstract class AbstractBeanPropertyFactory implements BeanPropertyFactory {

    private final ConcurrentMap<Class<?>, ConcurrentMap<String, Optional<BeanProperty>>> propertiesCache = new ConcurrentHashMap<>();
    
    /**
     * 根据指定类型与类属性，创建一个对应的BeanProperty
     *
     * @param targetClass 类型
     * @param field 属性
     * @return top.xiajibagao.crane.core.helper.property.BeanProperty
     * @author huangchengxing
     * @date 2022/6/2 8:16
     */
    protected abstract BeanProperty createBeanProperty(Class<?> targetClass, Field field);

    /**
     * 获取一个BeanProperty
     *
     * @param targetClass 目标类型
     * @param fieldName 字段名
     * @return java.util.Optional<top.xiajibagao.crane.core.helper.property.BeanProperty>
     * @author huangchengxing
     * @date 2022/5/10 16:05
     */
    @Override
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
        return Optional.ofNullable(createBeanProperty(targetClass, field));
    }

}
