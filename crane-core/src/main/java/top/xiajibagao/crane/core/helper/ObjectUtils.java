package top.xiajibagao.crane.core.helper;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author huangchengxing
 * @date 2022/02/26 18:11
 */
public class ObjectUtils {
    
    private ObjectUtils() {
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
        } catch (Throwable e) {
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
        } catch (Throwable e) {
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
        } catch (Throwable e) {
            failAction.accept(e);
        }
    }

}
