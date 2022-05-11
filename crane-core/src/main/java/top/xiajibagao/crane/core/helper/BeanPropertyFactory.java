package top.xiajibagao.crane.core.helper;

import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
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

    /**
     * 获取一个BeanProperty
     *
     * @param targetClass 目标类型
     * @param fieldName 字段名
     * @return java.util.Optional<top.xiajibagao.crane.core.helper.BeanProperty>
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
