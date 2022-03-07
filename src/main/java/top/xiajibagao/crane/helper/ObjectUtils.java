package top.xiajibagao.crane.helper;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

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

}
