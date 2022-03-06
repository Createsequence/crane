package top.xiajibagao.crane.helper;

import com.sun.javafx.fxml.BeanAdapter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author huangchengxing
 * @date 2022/03/02 14:08
 */
public class PropertyDescriptorUtils {

    private PropertyDescriptorUtils() {
    }

    private static final TableMap<Class<?>, String, PropertyCache> cache = new TableMap<>();

    /**
     * 从缓存中获取属性描述器缓存对象，若不存在则先创建缓存
     *
     * @param targetClass 目标类型
     * @return java.util.Optional<top.xiajibagao.crane.helper.PropertyCache>
     * @author huangchengxing
     * @date 2022/3/2 14:19
     */
    public static Optional<PropertyCache> getCache(Class<?> targetClass, String propertyName) {
        Map<String, PropertyCache> propertyCaches = cache.computeIfAbsent(targetClass, PropertyDescriptorUtils::createCache);
        if (CollectionUtils.isEmpty(propertyCaches)) {
            return Optional.empty();
        }
        return Optional.ofNullable(propertyCaches.get(propertyName));
    }

    /**
     * 从缓存中获取属性描述器缓存对象，若不存在则先创建缓存
     *
     * @param targetClass 目标类型
     * @return java.util.Map<java.lang.String,top.xiajibagao.crane.helper.PropertyDescriptorUtils.PropertyCache>
     * @author huangchengxing
     * @date 2022/3/2 14:19
     */
    public static Map<String, PropertyCache> getPropertyCaches(Class<?> targetClass) {
        return cache.computeIfAbsent(targetClass, PropertyDescriptorUtils::createCache);
    }

    /**
     * 解析类，获取属性描述器缓存
     *
     * @param targetClass 目标类型
     * @author huangchengxing
     * @date 2022/3/2 14:10
     */
    private static Map<String, PropertyCache> createCache(Class<?> targetClass) {
        return Stream.of(targetClass.getDeclaredFields())
            .map(f -> BeanUtils.getPropertyDescriptor(targetClass, f.getName()))
            .filter(Objects::nonNull)
            .map(d -> new PropertyCache(d, targetClass))
            .collect(Collectors.toMap(PropertyCache::getName, Function.identity()));
    }

    /**
     * 获取Setter方法，要求方法名为：“set + field.getName”，且仅有一个与field.getType()相同类型的参数
     *
     * @param targetClass 目标类型
     * @param fieldName 属性名
     * @param fieldType 属性类型
     * @return java.lang.reflect.Method
     * @author huangchengxing
     * @date 2022/3/6 14:52
     */
    public static Method findSetterMethod(Class<?> targetClass, String fieldName, Class<?> fieldType) {
        String methodName = getMethodName(BeanAdapter.SET_PREFIX, fieldName);
        return Stream.of(targetClass.getDeclaredMethods())
            .filter(m -> Objects.equals(m.getName(), methodName))
            .filter(m ->
                ArrayUtils.getLength(m.getParameterTypes()) == 1
                    && m.getParameterTypes()[0].isAssignableFrom(fieldType)
            )
            .findAny()
            .orElse(null);
    }

    private static String getMethodName(String prefix, String key) {
        return prefix + Character.toUpperCase(key.charAt(0)) + key.substring(1);
    }

}
