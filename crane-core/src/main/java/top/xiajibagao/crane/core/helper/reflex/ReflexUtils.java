package top.xiajibagao.crane.core.helper.reflex;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import top.xiajibagao.crane.core.helper.ObjectUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
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

    private static final BeanPropertyFactory REFLEX_PROPERTY_FACTORY = new BeanPropertyFactory(
        (targetClass, field) -> {
            Method getter = findGetterMethod(targetClass, field);
            Assert.notNull(getter, "属性{}找不到对应的Getter方法", field);
            Method setter = findSetterMethod(targetClass, field);
            Assert.notNull(setter, "属性{}找不到对应的Setter方法", field);
            return new ReflexBeanProperty(targetClass, field, getter, setter);
        }
    );
    public static final String GET_PREFIX = "get";
    public static final String SET_PREFIX = "set";
    public static final String IS_PREFIX = "is";

    private ReflexUtils() {
    }

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
     * @throws IllegalArgumentException 当属性不存在，或者属性存在但是却找不到对应setter与getter方法时抛出
     * @return cn.net.nova.crane.helper.PropertyUtils.PropertyCache
     * @author huangchengxing
     * @date 2022/4/1 13:50
     */
    public static Optional<BeanProperty> findProperty(Class<?> targetClass, String fieldName) {
        return REFLEX_PROPERTY_FACTORY.getProperty(targetClass, fieldName);
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
        return findField(targetClass, fieldName, false);
    }

    /**
     * 从指定类中获取属性
     *
     * @param targetClass 类
     * @param fieldName 属性名
     * @param mustExists 属性是否必须存在，为true时，若属性不存在则抛出异常
     * @return java.lang.reflect.Field
     * @author huangchengxing
     * @date 2022/4/1 13:39
     */
    @Nullable
    public static Field findField(Class<?> targetClass, String fieldName, boolean mustExists) {
        Field field = ReflectionUtils.findField(targetClass, fieldName);
        cn.hutool.core.lang.Assert.isTrue(!mustExists || Objects.nonNull(field), "类[{}]找不到名为[{}]的属性", targetClass, fieldName);
        return field;
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
     *     <li>若找不到，再寻找格式为“isFieldName”，并且没有参数的方法；</li>
     *     <li>仍然找不到，再寻找格式为“fieldName”，并且没有参数的方法；</li>
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
        Method getter = findFromClass(
            targetClass, Class::getDeclaredMethods,
            method -> Objects.equals(method.getName(), getStandardMethodName(GET_PREFIX, fieldName))
                && method.getParameterTypes().length == 0
        );
        if (Objects.nonNull(getter)) {
            return getter;
        }

        // 找不到则尝试寻找"isFieldName"方法
        getter = findFromClass(
            targetClass, Class::getDeclaredMethods,
            method -> Objects.equals(method.getName(), getStandardMethodName(IS_PREFIX, fieldName))
                && method.getParameterTypes().length == 0
        );

        // 仍然获取不到，则尝试寻找“fieldName()”方法
        return Objects.nonNull(getter) ? getter : findFromClass(
            targetClass, Class::getDeclaredMethods,
            method -> Objects.equals(method.getName(), fieldName)
                && method.getParameterTypes().length == 0
        );
    }

    /**
     * 从指定类及其父类中寻找指定属性的getter方法
     * <ul>
     *     <li>优先寻找格式为“getFieldName”，并且没有参数的方法；</li>
     *     <li>若找不到，再寻找格式为“isFieldName”，并且没有参数的方法；</li>
     *     <li>仍然找不到，再寻找格式为“fieldName”，并且没有参数的方法；</li>
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
     * 递归遍历当前类，当前类的父类及其实现的接口
     *
     * @param targetClass 类
     * @param classOperate 对类的操作，入参不会重复
     * @author huangchengxing
     * @date 2022/5/21 20:48
     */
    public static void forEachClass(Class<?> targetClass, Consumer<Class<?>> classOperate) {
        Deque<Class<?>> classDeque = CollUtil.newLinkedList(targetClass);
        Set<Class<?>> operatedClass = new HashSet<>();
        while (!classDeque.isEmpty()) {
            Class<?> target = classDeque.removeFirst();
            // 若当前类已经访问过，则无需再次处理
            if (operatedClass.contains(target)) {
                continue;
            }
            operatedClass.add(target);
            classOperate.accept(target);

            // 父类
            Class<?> superClass = target.getSuperclass();
            if (!Objects.equals(superClass, Object.class) && Objects.nonNull(superClass)) {
                classDeque.addLast(superClass);
            }
            // 接口
            Class<?>[] interfaces = target.getInterfaces();
            if (ArrayUtil.isNotEmpty(interfaces)) {
                CollUtil.addAll(classDeque, interfaces);
            }
        }
    }

    /**
     * 从类及其父类中操作指定的元素
     *
     * @param targetClass 类
     * @param targetMapping 指定元素的映射方法
     * @param consumer 操作，操作接受到的参数皆不为null
     * @author huangchengxing
     * @date 2022/4/1 12:58
     */
    public static <T> void forEachFromClass(
        Class<?> targetClass, Function<Class<?>, T[]> targetMapping, Consumer<T> consumer) {
        while (!Objects.equals(targetClass, Object.class) && Objects.nonNull(targetClass)) {
            T[] targets = targetMapping.apply(targetClass);
            if (Objects.nonNull(targets)) {
                for (T t : targets) {
                    ObjectUtils.acceptIfNotNull(t, consumer);
                }
            }
            targetClass = targetClass.getSuperclass();
        }
    }

    /**
     * 从类及其父类中寻找指定的元素
     *
     * @param targetClass 类
     * @param targetMapping 指定元素的映射方法
     * @param predicate 是否返回，接受到的参数有可能为null
     * @return T
     * @author huangchengxing
     * @date 2022/4/1 12:58
     */
    @Nullable
    public static <T> T findFromClass(
        Class<?> targetClass, Function<Class<?>, T[]> targetMapping, Predicate<T> predicate) {
        while (!Objects.equals(targetClass, Object.class) && Objects.nonNull(targetClass)) {
            T[] targets = targetMapping.apply(targetClass);
            if (Objects.nonNull(targets)) {
                for (T t : targets) {
                    if (predicate.test(t)) {
                        return t;
                    }
                }
            }
            targetClass = targetClass.getSuperclass();
        }
        return null;
    }

    /**
     * 获取标准的“prefixXXX”方法
     */
    private static String getStandardMethodName(String prefix, String name) {
        return CharSequenceUtil.upperFirstAndAddPre(name, prefix);
    }

    /**
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
        private final Method getter;
        private final Method setter;

        @Override
        public Object getValue(Object target) {
            return ObjectUtils.computeIfNotNull(target, t -> ReflectionUtils.invokeMethod(getter, t));
        }

        @Override
        public void setValue(Object target, Object value) {
            ReflectionUtils.invokeMethod(setter, target, value);
        }

    }
}
