package top.xiajibagao.crane.core.helper;

import org.springframework.lang.NonNull;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * {@link Collection}与{@link Map}集合工具类 <br />
 *
 * @author huangchengxing
 * @date 2021/10/19 11:30
 */
public final class CollUtils {

	private CollUtils() {
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

}
