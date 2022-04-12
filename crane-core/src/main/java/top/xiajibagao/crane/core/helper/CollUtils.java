package top.xiajibagao.crane.core.helper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Consumer3;
import cn.hutool.core.stream.StreamUtil;
import org.springframework.lang.NonNull;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link Collection}与{@link Map}集合工具类 <br />
 *
 * @author huangchengxing
 * @date 2021/10/19 11:30
 */
public final class CollUtils {

	private CollUtils() {
	}

	public static <T> Stream<T> of(Collection<T> source) {
		return CollUtil.defaultIfEmpty(source, Collections.emptyList()).stream();
	}

	@SafeVarargs
	public static <T> List<T> asList(T... targets) {
		return ObjectUtils.computeIfNotNull(targets, Arrays::asList, Collections.emptyList());
	}

	/**
	 * 集合中的非空元素是否有任意一项符合条件，若集合为空，则返回false
	 *
	 * @param source 源集合
	 * @param predicate 校验
	 * @param ifEmpty 当集合为空的时返回值
	 * @return boolean
	 * @author huangchengxing
	 * @date 2022/4/11 10:09
	 */
	public static <T> boolean anyMatch(Collection<T> source, Predicate<T> predicate, boolean ifEmpty) {
		return CollUtil.isNotEmpty(source) ? source.stream()
			.filter(Objects::nonNull)
			.anyMatch(predicate) : ifEmpty;
	}

	/**
	 * 集合中的非空元素是否有任意一项符合条件，若集合为空，则返回false
	 *
	 * @param targets 源集合
	 * @param predicate 校验
	 * @param ifEmpty 当集合为空的时返回值
	 * @return boolean
	 * @author huangchengxing
	 * @date 2022/4/11 10:09
	 */
	@SafeVarargs
	public static <T> boolean anyMatch(Predicate<T> predicate, boolean ifEmpty, T... targets) {
		return anyMatch(asList(targets), predicate, ifEmpty);
	}

	/**
	 * 集合中的非空元素是否全部符合条件，若集合为空，则返回false
	 *
	 * @param source 源集合
	 * @param predicate 校验
	 * @param ifEmpty 当集合为空的时返回值
	 * @return boolean
	 * @author huangchengxing
	 * @date 2022/4/11 10:09
	 */
	public static <T> boolean allMatch(Collection<T> source, Predicate<T> predicate, boolean ifEmpty) {
		return CollUtil.isNotEmpty(source) ? source.stream()
			.filter(Objects::nonNull)
			.allMatch(predicate) : ifEmpty;
	}

	/**
	 * 集合中的非空元素是否全部符合条件，若集合为空，则返回false
	 *
	 * @param targets 源集合
	 * @param predicate 校验
	 * @param ifEmpty 当集合为空的时返回值
	 * @return boolean
	 * @author huangchengxing
	 * @date 2022/4/11 10:09
	 */
	@SafeVarargs
	public static <T> boolean allMatch(Predicate<T> predicate, boolean ifEmpty, T... targets) {
		return anyMatch(asList(targets), predicate, ifEmpty);
	}

	/**
	 * 集合中的非空元素是否全部不符合条件，若集合为空，则返回true
	 *
	 * @param source 源集合
	 * @param predicate 校验
	 * @param ifEmpty 当集合为空的时返回值
	 * @return boolean
	 * @author huangchengxing
	 * @date 2022/4/11 10:09
	 */
	public static <T> boolean noneMatch(Collection<T> source, Predicate<T> predicate, boolean ifEmpty) {
		return CollUtil.isNotEmpty(source) ? source.stream()
			.filter(Objects::nonNull)
			.noneMatch(predicate) : ifEmpty;
	}

	/**
	 * 集合中的非空元素是否全部不符合条件，若集合为空，则返回true
	 *
	 * @param predicate 校验
	 * @param ifEmpty 当集合为空的时返回值
	 * @param targets 源集合
	 * @return boolean
	 * @author huangchengxing
	 * @date 2022/4/11 10:09
	 */
	@SafeVarargs
	public static <T> boolean noneMatch(Predicate<T> predicate, boolean ifEmpty, T... targets) {
		return noneMatch(asList(targets), predicate, ifEmpty);
	}

	/**
	 * 将一个集合转为另一指定集合, 总是过滤转换源集合与转换后的集合中为null的元素 <br />
	 * eg:
	 * <pre>{@code
	 * 		List<String> stringList = Arrays.asList("123", null, "424", "233", null);
	 * 		System.out.println(stringList); // [123, null, 424, 233, null]
	 * 		Set<Integer> integerSet = CollUtils.toCollection(stringList, LinkedHashSet::new, Integer::parseInt);
	 * 		System.out.println(integerSet); // [123, 424, 233]
	 * }<pre/>
	 *
	 * @param source 源集合
	 * @param collFactory 指定集合的生成方法, 生成的集合不能为null
	 * @param mapper 映射方法
	 * @param <S> 源集合元素类型
	 * @param <T> 返回集合元素类型
	 * @param <C> 返回集合类型
	 * @return C 转换集合
	 * @author huangchengxing
	 * @date 2021/9/15 10:43
	 */
	@NonNull
	public static <S, T, C extends Collection<T>> C toCollection(
		@NonNull Collection<S> source,
		@NonNull Supplier<C> collFactory,
		@NonNull Function<S, T> mapper) {
		return source.stream()
			.filter(Objects::nonNull)
			.map(mapper)
			.filter(Objects::nonNull)
			.collect(Collectors.toCollection(collFactory));
	}

	/**
	 * 将一个集合转为另一HashSet集合
	 *
	 * @param source 源集合
	 * @param mapper 映射方法
	 * @param <S> 源集合元素类型
	 * @param <T> 返回集合元素类型
	 * @return java.util.Set<T> 转换集合
	 * @author huangchengxing
	 * @date 2021/10/19 10:25
	 */
	@NonNull
	public static <S, T> Set<T> toSet(Collection<S> source, @NonNull Function<S, T> mapper) {
		if (source == null || source.isEmpty()) {
			return Collections.emptySet();
		}
		return toCollection(source, HashSet::new, mapper);
	}

	/**
	 * 将一个集合转为HashSet集合
	 *
	 * @param source 源集合
	 * @param <T> 集合元素类型
	 * @return java.util.Set<T> 转换集合
	 * @author huangchengxing
	 * @date 2021/10/19 10:25
	 */
	@NonNull
	public static <T> Set<T> toSet(Collection<T> source) {
		return toSet(source, Function.identity());
	}

	/**
	 * 将一个集合转为ArrayList集合
	 *
	 * @param source 源集合
	 * @param <T> 返回集合元素类型
	 * @return java.util.Set<T> 转换集合
	 * @author huangchengxing
	 * @date 2021/10/19 10:25
	 */
	@NonNull
	public static <T> List<T> toList(Collection<T> source) {
		if (source == null || source.isEmpty()) {
			return Collections.emptyList();
		}
		return toCollection(source, ArrayList::new, Function.identity());
	}

	/**
	 * 将Object数据适配为Collection集合
	 * <ul>
	 *     <li>null: 返回{@link Collections#emptyList()}</li>
	 *     <li>Object: 适配为{@link Collections#singletonList(Object)}</li>
	 *     <li>Map: 适配为{@link Map#entrySet()}；</li>
	 *     <li>Collection: 强转为Collection集合</li>
	 *     <li>Array: 使用{@link Arrays#asList(Object[])}转为集合；</li>
	 * </ul>
	 *
	 * @param target 目标数据
	 * @return java.util.Collection<?>
	 * @author huangchengxing
	 * @date 2021/12/8 16:26
	 */
	public static Collection<?> adaptToCollection(Object target) {
		if (Objects.isNull(target)) {
			return Collections.emptyList();
		}
		if (target instanceof Collection) {
			return (Collection<?>) target;
		}
		if (target instanceof Map) {
			return ((Map<?, ?>) target).entrySet();
		}
		if(target.getClass().isArray()) {
			return Arrays.asList((Object[])target);
		}
		return Collections.singletonList(target);
	}

	/**
	 * 遍历集合中的非空元素
	 *
	 * @param source 源集合
	 * @param consumer 操作
	 * @author huangchengxing
	 * @date 2022/4/11 10:12
	 */
	public static <T> void forEach(Iterable<T> source, Consumer<T> consumer) {
		if (CollUtil.isEmpty(source)) {
			return;
		}
		for (T t : source) {
			if (Objects.nonNull(t)) {
				consumer.accept(t);
			}
		}
	}

	/**
	 * 遍历集合中的非空元素
	 *
	 * @param source 源集合
	 * @param consumer 操作
	 * @author huangchengxing
	 * @date 2022/4/11 10:12
	 */
	public static <T> void flatForEach(Iterable<Iterable<T>> source, Consumer<T> consumer) {
		if (CollUtil.isEmpty(source)) {
			return;
		}
		StreamUtil.of(source)
			.filter(CollUtil::isNotEmpty)
			.flatMap(StreamUtil::of)
			.filter(Objects::nonNull)
			.forEach(consumer);
	}

	/**
	 * 遍历两个集合中的非空元素
	 *
	 * @param source1 源集合1
	 * @param source2 源集合2
	 * @param consumer 操作
	 * @author huangchengxing
	 * @date 2022/4/11 10:12
	 */
	public static <T, R> void biForEach(Iterable<T> source1, Iterable<R> source2, BiConsumer<T, R> consumer) {
		if (CollUtil.isEmpty(source1) || CollUtil.isEmpty(source2)) {
			return;
		}
		for (T t : source1) {
			if (Objects.isNull(t)) {
				continue;
			}
			for (R r : source2) {
				if (Objects.isNull(r)) {
					continue;
				}
				consumer.accept(t, r);
			}
		}
	}

	/**
	 * 遍历两个集合中的非空元素
	 *
	 * @param source 源集合
	 * @param consumer 操作
	 * @author huangchengxing
	 * @date 2022/4/11 10:12
	 */
	public static <R, C, V> void forEach(Map<R, ? extends Map<C, V>> source, Consumer3<R, C, V> consumer) {
		if (CollUtil.isEmpty(source)) {
			return;
		}
		source.forEach((rowKey, colMap) -> colMap.forEach((colKey, val) -> consumer.accept(rowKey, colKey, val)));
	}

	/**
	 * 遍历集合中的非空元素
	 *
	 * @param source 源集合
	 * @param consumer 操作
	 * @author huangchengxing
	 * @date 2022/4/11 10:12
	 */
	public static <K, V> void forEach(Map<K, ? extends Collection<V>> source, BiConsumer<K, V> consumer) {
		if (CollUtil.isEmpty(source)) {
			return;
		}
		source.forEach((k ,vs) -> vs.forEach(v -> {
			if (Objects.nonNull(k) && Objects.nonNull(v)) {
				consumer.accept(k, v);
			}
		}));
	}

}
