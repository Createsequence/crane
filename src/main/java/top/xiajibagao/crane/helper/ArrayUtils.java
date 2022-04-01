package top.xiajibagao.crane.helper;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author huangchengxing
 * @date 2022/03/01 17:30
 */
public class ArrayUtils {

    private ArrayUtils() {
    }

    @SafeVarargs
    public static <T> int getLength(T... array) {
        return ObjectUtils.computeIfNotNull(array, a -> a.length, 0);
    }

    @SafeVarargs
    public static <T> boolean isEmpty(T... targets) {
        return Objects.isNull(targets) || targets.length == 0;
    }

    @SafeVarargs
    public static <T> boolean isNotEmpty(T... targets) {
        return !isEmpty(targets);
    }

    public static boolean notEmpty(Object... targets) {
        return !isEmpty(targets);
    }

    @SafeVarargs
    public static <T> List<T> toList(T... targets) {
        return isEmpty(targets) ? Collections.emptyList() : Arrays.asList(targets);
    }

    public static int size(Object... targets) {
        return Objects.isNull(targets) ? 0 : targets.length;
    }

    public static <T> T[] merge(Collection<T> coll, T... appends) {
        Collection<T> appendList = ObjectUtils.computeIfNotNull(appends, Arrays::asList, Collections.emptyList());
        Object[] targets = Stream.of(coll, appendList)
            .filter(CollUtils::isNotEmpty)
            .collect(Collectors.toList())
            .toArray();
        return caseType(targets);
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] caseType(Object[]... targets) {
        return (T[]) targets;
    }

}
