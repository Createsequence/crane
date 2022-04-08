package top.xiajibagao.crane.core.helper;

import cn.hutool.core.util.ArrayUtil;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 反射工具类
 *
 * @author huangchengxing
 * @date 2022/04/01 18:34
 */
public class ReflexUtils {

    private ReflexUtils() {}

    private static final Table<Class<?>, String, BeanProperty> CACHE_TABLE = HashBasedTable.create();
    public static final String GET_PREFIX = "get";
    public static final String SET_PREFIX = "set";

    /**
     * 从类中查找指定方法
     *
     * @param targetClass 类
     * @param methodName 方法名称
     * @param allowSubclasses 参数类型是否允许子类
     * @param returnType 返回值类型
     * @param paramTypes 参数类型
     * @return java.lang.reflect.Method
     * @author huangchengxing
     * @date 2022/4/1 17:28
     */
    @Nullable
    public static Method findMethod(
        Class<?> targetClass, String methodName, boolean allowSubclasses, Class<?> returnType, Class<?>... paramTypes) {
        return findFromClass(targetClass, Class::getDeclaredMethods, method -> {
            // 方法名是否匹配
            if (!Objects.equals(method.getName(), methodName)) {
                return false;
            }
            // 返回值是否匹配
            boolean returnTypeMatched = allowSubclasses ?
                ClassUtils.isAssignable(returnType, method.getReturnType()) : Objects.equals(returnType, method.getReturnType());
            if (!returnTypeMatched) {
                return false;
            }
            // 参数是否匹配
            if (ArrayUtil.isNotEmpty(paramTypes)) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (paramTypes.length != parameterTypes.length) {
                    return false;
                }
                for (int i = 0; i < parameterTypes.length; i++) {
                    boolean paramTypeMatched = allowSubclasses ?
                        ClassUtils.isAssignable(paramTypes[i], parameterTypes[i]) : Objects.equals(paramTypes[i], parameterTypes[i]);
                    if (!paramTypeMatched) {
                        return false;
                    }
                }
            }
            return true;
        });
    }

    /**
     * 获取字段缓存
     *
     * @param targetClass 类
     * @param fieldName 属性名
     * @throws IllegalArgumentException 当属性存在，而却找不到对应setter与getter方法时抛出
     * @return cn.net.nova.crane.helper.PropertyUtils.PropertyCache
     * @author huangchengxing
     * @date 2022/4/1 13:50
     */
    public static Optional<BeanProperty> findProperty(Class<?> targetClass, String fieldName) {
        BeanProperty cache = CACHE_TABLE.get(targetClass, fieldName);
        if (Objects.isNull(cache)) {
            cache = createProperty(targetClass, fieldName);
        }
        return Optional.ofNullable(cache);
    }

    /**
     * 创建字段缓存
     */
    @Nullable
    private static BeanProperty createProperty(Class<?> targetClass, String fieldName) {
        Field field = findField(targetClass, fieldName);
        if (Objects.isNull(field)) {
            return null;
        }
        Method getter = findGetterMethod(targetClass, field);
        Assert.notNull(getter, String.format("属性[%s]找不到对应的Getter方法", field));
        Method setter = findSetterMethod(targetClass, field);
        Assert.notNull(setter, String.format("属性[%s]找不到对应的Setter方法", field));
        return new BeanProperty(targetClass, field, getter, setter);
    }

    /**
     * 从指定类中获取属性
     *
     * @param targetClass 类
     * @param fieldName 属性名
     * @return java.lang.reflect.Field
     * @author huangchengxing
     * @date 2022/4/1 13:39
     */
    @Nullable
    public static Field findField(Class<?> targetClass, String fieldName) {
        return ReflectionUtils.findField(targetClass, fieldName);
    }

    /**
     * 从指定类及其父类中寻找指定属性的setter方法
     * <ul>
     *     <li>优先寻找格式为“setFieldName”，并且有且仅有一个类型为fieldType的方法；</li>
     *     <li>若找不到，再寻找格式为“fieldName”，并且有且仅有一个类型为fieldType的方法；</li>
     * </ul>
     *
     * @param targetClass 属性
     * @param fieldName 属性名
     * @return java.lang.reflect.Method
     * @author huangchengxing
     * @date 2022/4/1 12:58
     */
    @Nullable
    public static Method findSetterMethod(Class<?> targetClass, String fieldName, Class<?> fieldType) {
        // 尝试获取"setFieldName"方法
        String standardMethodName = getStandardMethodName(SET_PREFIX, fieldName);
        Method getter = findFromClass(
            targetClass,
            Class::getDeclaredMethods,
            method -> Objects.equals(method.getName(), standardMethodName)
                && method.getParameterTypes().length == 1
                && ClassUtils.isAssignable(fieldType, method.getParameterTypes()[0])
        );
        if (Objects.nonNull(getter)) {
            return getter;
        }

        // 找不到则尝试寻找"fieldName()"方法
        return findFromClass(
            targetClass, Class::getDeclaredMethods,
            method -> Objects.equals(method.getName(), fieldName)
                && method.getParameterTypes().length == 1
                && ClassUtils.isAssignable(fieldType, method.getParameterTypes()[0])
        );
    }

    /**
     * 从指定类及其父类中寻找指定属性的setter方法
     * <ul>
     *     <li>优先寻找格式为“setFieldName”，并且有且仅有一个类型为fieldType的方法；</li>
     *     <li>若找不到，再寻找格式为“fieldName”，并且有且仅有一个类型为fieldType的方法；</li>
     * </ul>
     *
     * @param targetClass 类
     * @param field 属性
     * @return java.lang.reflect.Method
     * @author huangchengxing
     * @date 2022/4/1 12:58
     */
    @Nullable
    public static Method findSetterMethod(Class<?> targetClass, Field field) {
        return findSetterMethod(targetClass, field.getName(), field.getType());
    }

    /**
     * 从指定类及其父类中寻找指定属性的getter方法
     * <ul>
     *     <li>优先寻找格式为“getFieldName”，并且没有参数的方法；</li>
     *     <li>若找不到，再寻找格式为“fieldName”，并且没有参数的方法；</li>
     * </ul>
     *
     * @param targetClass 类
     * @param fieldName 属性名
     * @return java.lang.reflect.Method
     * @author huangchengxing
     * @date 2022/4/1 12:58
     */
    @Nullable
    public static Method findGetterMethod(Class<?> targetClass, String fieldName) {
        // 尝试获取"getFieldName"方法
        String standardMethodName = getStandardMethodName(GET_PREFIX, fieldName);
        Method getter = findFromClass(
            targetClass,
            Class::getDeclaredMethods,
            method -> Objects.equals(method.getName(), standardMethodName)
                && method.getParameterTypes().length == 0
        );
        if (Objects.nonNull(getter)) {
            return getter;
        }

        // 找不到则尝试寻找"fieldName()"方法
        return findFromClass(
            targetClass, Class::getDeclaredMethods,
            method -> Objects.equals(method.getName(), fieldName)
                && method.getParameterTypes().length == 0
        );
    }

    /**
     * 从指定类及其父类中寻找指定属性的getter方法
     * <ul>
     *     <li>优先寻找格式为“getFieldName”，并且没有参数的方法；</li>
     *     <li>若找不到，再寻找格式为“fieldName”，并且没有参数的方法；</li>
     * </ul>
     *
     * @param targetClass 类
     * @param field 属性
     * @return java.lang.reflect.Method
     * @author huangchengxing
     * @date 2022/4/1 12:58
     */
    @Nullable
    public static Method findGetterMethod(Class<?> targetClass, Field field) {
        return findGetterMethod(targetClass, field.getName());
    }

    /**
     * 从类及其父类中操作指定的元素
     *
     * @param targetClass 类
     * @param targetMapping 指定元素的映射方法
     * @param consumer 操作
     * @author huangchengxing
     * @date 2022/4/1 12:58
     */
    public static <T> void forEachFromClass(
        Class<?> targetClass, Function<Class<?>, T[]> targetMapping, Consumer<T> consumer) {
        while (!Objects.equals(targetClass, Object.class) && Objects.nonNull(targetClass)) {
            T[] targets = targetMapping.apply(targetClass);
            for (T t : targets) {
                consumer.accept(t);
            }
            targetClass = targetClass.getSuperclass();
        }
    }

    /**
     * 从类及其父类中寻找指定的元素
     *
     * @param targetClass 类
     * @param targetMapping 指定元素的映射方法
     * @param predicate 是否返回
     * @return T
     * @author huangchengxing
     * @date 2022/4/1 12:58
     */
    public static <T> T findFromClass(
        Class<?> targetClass, Function<Class<?>, T[]> targetMapping, Predicate<T> predicate) {
        while (!Objects.equals(targetClass, Object.class) && Objects.nonNull(targetClass)) {
            T[] targets = targetMapping.apply(targetClass);
            for (T t : targets) {
                if (predicate.test(t)) {
                    return t;
                }
            }
            targetClass = targetClass.getSuperclass();
        }
        return null;
    }

    /**
     * 获取标准的“getXXX”与“setXXX”方法
     */
    private static String getStandardMethodName(String prefix, String key) {
        return prefix + Character.toUpperCase(key.charAt(0)) + key.substring(1);
    }

}
