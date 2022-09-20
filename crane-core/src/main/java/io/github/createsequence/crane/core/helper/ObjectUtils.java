package io.github.createsequence.crane.core.helper;

import cn.hutool.core.collection.CollUtil;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 通用工具类，提供针对对象操作的一些函数式操作支持
 *
 * @author huangchengxing
 * @date 2022/02/26 18:11
 */
public class ObjectUtils {
    
    private ObjectUtils() {
    }
    
    /**
     * 获取对象类型，若对象为集合，则尝试获取第一个对象的类型
     *
     * @param target 对象
     * @return java.lang.Class<?>
     * @author huangchengxing
     * @date 2022/7/9 17:45
     */
    public static Class<?> getClass(Object target) {
        // 适配为集合
        Collection<?> targets = CollUtils.adaptToCollection(target);
        if (CollUtil.isEmpty(targets)) {
            return null;
        }
        // 获取类型
        return ObjectUtils.computeIfNotNull(CollUtil.getFirst(targets), Object::getClass);
    }

    /**
     * 若指定值不为空，则获取该值，否则获取默认值
     *
     * @param target 指定值
     * @param def 默认值
     * @return T
     * @author huangchengxing
     * @date 2022/3/1 13:38
     */
    public static <T> T defaultIfNull(T target, T def) {
        return Objects.isNull(target) ? def : target;
    }

    /**
     * 若校验通过，则将其映射并返回另一值，否则返回默认值
     *
     * @param target 指定值
     * @param predicate 校验
     * @param mapping 映射
     * @param def 默认值
     * @author huangchengxing
     * @date 2022/3/1 13:39
     */
    public static <T, R> R supplyIfRight(T target, Predicate<T> predicate, Function<T, R> mapping, R def) {
        if (predicate.test(target)) {
            mapping.apply(target);
        }
        return def;
    }

    /**
     * 若校验不通过，则将其映射并返回另一值，否则返回默认值
     *
     * @param target 指定值
     * @param predicate 校验
     * @param mapping 映射
     * @param def 默认值
     * @author huangchengxing
     * @date 2022/3/1 13:39
     */
    public static <T, R> R supplyIfFalse(T target, Predicate<T> predicate, Function<T, R> mapping, R def) {
        return supplyIfRight(target, predicate.negate(), mapping, def);
    }

    /**
     * 若校验通过，则对其操作
     *
     * @param target 指定值
     * @param predicate 校验
     * @param consumer 操作
     * @author huangchengxing
     * @date 2022/3/1 13:39
     */
    public static <T> void acceptIfRight(T target, Predicate<T> predicate, Consumer<T> consumer) {
        if (predicate.test(target)) {
            consumer.accept(target);
        }
    }

    /**
     * 若校验不通过，则对其操作
     *
     * @param target 指定值
     * @param predicate 校验
     * @param consumer 操作
     * @author huangchengxing
     * @date 2022/3/1 13:39
     */
    public static <T> void acceptIfFalse(T target, Predicate<T> predicate, Consumer<T> consumer) {
        acceptIfRight(target, predicate.negate(), consumer);
    }

    /**
     * 若指定值不为空，则对其操作
     *
     * @param target 指定值
     * @param consumer 操作
     * @author huangchengxing
     * @date 2022/3/1 13:39
     */
    public static <T> void acceptIfNotNull(T target, Consumer<T> consumer) {
        if (Objects.nonNull(target)) {
            consumer.accept(target);
        }
    }

    /**
     * 若指定值不为空，则将其映射另一值并返回，若前者或后者为空则都将返回默认值
     *
     * @param target 指定值
     * @param mapping 映射方法
     * @param def 默认值
     * @return R
     * @author huangchengxing
     * @date 2022/3/1 13:39
     */
    public static <T, R> R computeIfNotNull(T target, Function<T, R> mapping, R def) {
        return Objects.isNull(target) ? def : defaultIfNull(mapping.apply(target), def);
    }

    /**
     * 若指定值不为空，则将其映射另一值并返回，若前者或后者为空则都将返回null
     *
     * @param target 指定值
     * @param mapping 映射方法
     * @return R
     * @author huangchengxing
     * @date 2022/3/1 13:39
     */
    public static <T, R> R computeIfNotNull(T target, Function<T, R> mapping) {
        return computeIfNotNull(target, mapping, null);
    }

    /**
     * 若校验为true，则将其映射另一值并返回，若前者或后者为空则都将返回默认值
     *
     * @param target 指定值
     * @param predicate 校验
     * @param mapping 映射方法
     * @param def 默认值
     * @return R
     * @author huangchengxing
     * @date 2022/3/1 13:39
     */
    public static <T, R> R computeIfMatch(T target, Predicate<T> predicate, Function<T, R> mapping, R def) {
        if (Objects.nonNull(target) && predicate.test(target)) {
            R result = mapping.apply(target);
            return defaultIfNull(result, def);
        }
        return def;
    }

    /**
     * 若校验为true，则将其映射另一值并返回，若前者或后者为空则都将返回默认值
     *
     * @param target 指定值
     * @param predicate 校验
     * @param mapping 映射方法
     * @return R
     * @author huangchengxing
     * @date 2022/3/1 13:39
     */
    public static <T, R> R computeIfMatch(T target, Predicate<T> predicate, Function<T, R> mapping) {
        return computeIfMatch(target, predicate, mapping, null);
    }
    
    /**
     * 尝试进行一次操作
     *
     * @param runnable 操作
     * @param failAction 错误响应
     * @author huangchengxing
     * @date 2022/3/23 21:14
     */
    public static void tryAction(Runnable runnable, Consumer<Throwable> failAction) {
        try {
            runnable.run();
        } catch (Exception e) {
            failAction.accept(e);
        }
    }

    /**
     * 尝试进行一次生成
     *
     * @param supplier 操作
     * @param failAction 错误响应
     * @author huangchengxing
     * @date 2022/3/23 21:14
     */
    public static <T> T trySupply(Supplier<T> supplier, Consumer<Throwable> failAction) {
        T result = null;
        try {
            result = supplier.get();
        } catch (Exception e) {
            failAction.accept(e);
        }
        return result;
    }

    /**
     * 尝试进行一次操作
     *
     * @param supplier 操作
     * @param failAction 错误响应
     * @author huangchengxing
     * @date 2022/3/23 21:14
     */
    public static <T> void tryAction(Supplier<T> supplier, Consumer<Throwable> failAction, Consumer<T> successAction) {
        try {
            T result = supplier.get();
            successAction.accept(result);
        } catch (Exception e) {
            failAction.accept(e);
        }
    }
    
    /**
     * 目标对象为指定类型或其子类的实例，则将其转为指定类型并追加操作
     *
     * @param target 目标对象
     * @param targetClass 指定类型
     * @param consumer 消费者
     * @return boolean 目标对象为指定类型或其子类的实例
     * @author huangchengxing
     * @date 2022/4/10 9:24
     */
    public static <T> boolean instanceOf(Object target, Class<T> targetClass, Consumer<T> consumer) {
        if (Objects.isNull(target) || !target.getClass().isAssignableFrom(targetClass)) {
            return false;
        }
        consumer.accept(targetClass.cast(target));
        return true;
    }

}
