package top.xiajibagao.crane.helper;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.*;
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

	public static boolean isNotEmpty(Collection<?> target) {
		return !CollectionUtils.isEmpty(target);
	}

	public static <T> T getFirst(Iterable<T> target) {
		if (Objects.isNull(target)) {
			return null;
		}
		if (target instanceof RandomAccess && target instanceof List) {
			List<T> coll = (List<T>)target;
			return coll.isEmpty() ? null : coll.get(0);
		}
		Iterator<T> iterator = target.iterator();
		return iterator.hasNext() ? iterator.next() : null;
	}

	// ============================ toMap ============================

	/**
	 * 将集合按指定条件映射为map集合
	 *
	 * @param source 源集合
	 * @param keyMapper key生成方法
	 * @param valueMapper value生成方法
	 * @return java.util.Map<K,V> 分组结果
	 * @param <T> 源集合元素类型
	 * @param <K> key类型
	 * @param <V> value类型
	 * @exception IllegalStateException 当存在相同key时抛出
	 * @author huangchengxing
	 * @date 2021/10/19 10:09
	 */
	@NotNull
	public static <T, K, V> Map<K, V> toMap(
		Collection<T> source,
		@NotNull Function<T, K> keyMapper,
		@NotNull Function<T, V> valueMapper) {

		if (CollectionUtils.isEmpty(source)) {
			return Collections.emptyMap();
		}
		return source.stream()
			.filter(Objects::nonNull)
			.filter(t -> Objects.nonNull(keyMapper.apply(t)))
			.filter(t -> Objects.nonNull(valueMapper.apply(t)))
			.collect(Collectors.toMap(keyMapper, valueMapper));
	}

	/**
	 * 将集合按指定条件映射为map集合
	 *
	 * @param source 源集合
	 * @param keyMapper key生成方法
	 * @return java.util.Map<K,V> 分组结果
	 * @param <T> 源集合元素类型
	 * @param <K> key类型
	 * @exception IllegalStateException 当存在相同key时抛出
	 * @author huangchengxing
	 * @date 2021/10/19 10:09
	 */
	@NotNull
	public static <T, K> Map<K, T> toMap(
		Collection<T> source,
		@NotNull Function<T, K> keyMapper) {
		return toMap(source, keyMapper, Function.identity());
	}

	/**
	 * 将一种类型的Map集合转换为另一种类型的Map集合 <br />
	 * eg:
	 * <pre>{@code
	 * 		Map<Integer, String> source = MapUtil.<Integer, String> builder()
	 * 			.put(123, "123")
	 * 			.put(233, "123")
	 * 			.put(456, "456")
	 * 			.build();
	 * 		Map<BigDecimal, List<String>> result = toMap(source, BigDecimal::new, Collections::singletonList, HashMap::new);
	 * 		System.out.println(result); // {233=[123], 123=[123], 456=[456]}
	 * }<pre/>
	 *
	 * @param source 源集合
	 * @param keyMapper key映射方法
	 * @param valueMapper value映射方法
	 * @param mapFactory 指定Map集合的生成方法, 生成的集合不能为null
	 * @param <A> 源集合key类型
	 * @param <B> 源集合value类型
	 * @param <C> 目标集合key类型
	 * @param <D> 目标集合value类型
	 * @return java.util.Map<C,D> 转换集合
	 * @exception IllegalStateException 当存在相同key时抛出
	 * @author huangchengxing
	 * @date 2021/10/19 17:23
	 */
	@NotNull
	public static <A, B, C, D> Map<C, D> toMap(
		Map<A, B> source,
		@NotNull Function<A, C> keyMapper,
		@NotNull Function<B, D> valueMapper,
		@NotNull Supplier<Map<C, D>> mapFactory) {

		if (CollectionUtils.isEmpty(source)) {
			return Collections.emptyMap();
		}

		Map<C, D> result = mapFactory.get();
		Objects.requireNonNull(result);
		source.forEach((k, v) -> checkDuplicateAndPut(result, keyMapper.apply(k), valueMapper.apply(v)));

		return result;
	}

	/**
	 * 将源集合的value映射到新的key上
	 *
	 * @param source 源集合
	 * @param keyMapper 映射方法
	 * @return java.util.Map<K2,V>
	 * @author huangchengxing
	 * @date 2022/1/12 13:40
	 */
	public static <K1, V, K2> Map<K2, V> mapKeys(Map<K1, V> source, Function<K1, K2> keyMapper) {
		return toMap(source, keyMapper, Function.identity(), HashMap::new);
	}

	/**
	 * 将源集合的value映射到新的key上
	 *
	 * @param source 源集合
	 * @param valueMapper 映射方法
	 * @return java.util.Map<K2,V>
	 * @author huangchengxing
	 * @date 2022/1/12 13:40
	 */
	public static <K, V1, V2> Map<K, V2> mapValues(Map<K, V1> source, Function<V1, V2> valueMapper) {
		return toMap(source, Function.identity(), valueMapper, HashMap::new);
	}

	// ============================ groupBy ============================

	/**
	 * 将集合按指定条件分组，再将对分组后得到的value集合转为另一指定类型的集合
	 *
	 * @param source 源集合
	 * @param keyMapper 分组key映射方法
	 * @param valueMapper 分组后value集合元素的映射方法
	 * @param collFactory 指定集合的生成方法, 生成的集合不能为null
	 * @param <T> 源集合元素类型
	 * @param <K> key类型
	 * @param <V> 转换value集合元素类型
	 * @param <C> 转换value集合类型
	 * @return java.util.Map<K,java.util.List<V>> 分组结果
	 * @author huangchengxing
	 * @date 2021/10/19 11:14
	 */
	@NotNull
	public static <T, K, V, C extends Collection<V>> Map<K, C> groupBy(
		Collection<T> source,
		@NotNull Function<T, K> keyMapper,
		@NotNull Function<T, V> valueMapper,
		@NotNull Supplier<C> collFactory) {

		if (CollectionUtils.isEmpty(source)) {
			return Collections.emptyMap();
		}
		return source.stream()
			.filter(Objects::nonNull)
			.filter(t -> Objects.nonNull(keyMapper.apply(t)))
			.collect(
				Collectors.groupingBy(
					keyMapper, HashMap::new,
					Collectors.mapping(
						valueMapper,
						Collectors.toCollection(collFactory)
					)
				)
			);
	}

	/**
	 * 将集合按指定条件分组
	 *
	 * @param source 源集合
	 * @param keyMapper 分组key映射方法
	 * @param <T> 源集合元素类型
	 * @param <K> key类型
	 * @return java.util.Map<K,java.util.List<V>> 分组结果
	 * @author huangchengxing
	 * @date 2021/10/19 11:14
	 */
	@NotNull
	public static <T, K> Map<K, List<T>> groupBy(Collection<T> source, @NotNull Function<T, K> keyMapper) {
		return groupBy(source, keyMapper, Function.identity(), ArrayList::new);
	}

	/**
	 * 将集合按指定规则分组，然后得到分组结果的val集合转为另一种集合，最后再对转换后的val集合进行映射 <br />
	 * eg:
	 * <pre>
	 *     List<String> source = Arrays.asList("123", "123", "233", "456");
	 *         Map<String, String> result = toMapAndThen(
	 *             source,
	 *             Function.identity(),
	 *             Integer::valueOf,
	 *             (id, list) -> "\"id: " + id + ", count: " + list.size() + "\""
	 *         );
	 *         System.out.println(result); // {456="id: 456, count: 1", 233="id: 233, count: 1", 123="id: 123, count: 2"}
	 * </pre>
	 *
	 * @param source 目标
	 * @param classify 分类方法
	 * @param valueMapper 对分组结果的val集合的转换方法
	 * @param mergeMapper 对转换后获得的集合的映射
	 * @param <S> 源集合元素类型
	 * @param <K> 分组后key类型
	 * @param <V> val转换后集合的元素类型
	 * @param <R> 转换后的val集合映射结果类型
	 * @return java.util.Map<K,R>
	 * @author huangchengxing
	 * @date 2021/10/22 15:31
	 */
	@NotNull
	public static <S, K, V, R> Map<K, R> groupByAndThen(
		Collection<S> source,
		@NotNull Function<S, K> classify,
		@NotNull Function<S, V> valueMapper,
		@NotNull BiFunction<K, List<V>, R> mergeMapper) {

		if (CollectionUtils.isEmpty(source)) {
			return Collections.emptyMap();
		}
		Map<K, List<V>> first = groupBy(source, classify, valueMapper, ArrayList::new);
		Map<K, R> then = new HashMap<>(first.size());
		first.forEach((k, v) -> then.put(k, mergeMapper.apply(k, v)));
		return then;
	}

	/**
	 * 将集合按指定规则分组，然后得到分组结果的val集合进行映射 <br />
	 * eg:
	 * <pre>
	 *     List<String> source = Arrays.asList("123", "123", "233", "456");
	 *     Map<String, String> result = toMapAndThen(
	 *         source,
	 *         Function.identity(),
	 *         (id, list) -> "\"id: " + id + ", count: " + list.size() + "\""
	 *     );
	 *     System.out.println(result); // {456="id: 456, count: 1", 233="id: 233, count: 1", 123="id: 123, count: 2"}
	 * </pre>
	 *
	 * @param source 目标
	 * @param classify 分类方法
	 * @param mergeMapper 对转换后获得的集合的映射
	 * @param <S> 源集合元素类型
	 * @param <K> 分组后key类型
	 * @param <R> 转换后的val集合映射结果类型
	 * @return java.util.Map<K,R>
	 * @author huangchengxing
	 * @date 2021/10/22 15:39
	 */
	@NotNull
	public static <S, K, R> Map<K, R> groupByAndThen(
		Collection<S> source,
		@NotNull Function<S, K> classify,
		@NotNull BiFunction<K, List<S>, R> mergeMapper) {
		return groupByAndThen(source, classify, Function.identity(), mergeMapper);
	}

	/**
	 * 将一组元素分组后转为为另一类型集合，然后对分组结果进行映射, 总是过滤映射后集合中为null的元素 <br />
	 * eg:
	 * <pre>{@code
	 * 		List<String> source = Arrays.asList("123", "156", "233", "244", "567");
	 * 		List<String> result = groupByThenFlat(
	 * 			source,
	 * 			str -> str.substring(0, 1),
	 * 			str -> "item: " + str,
	 * 			(key, values) -> key + ": " + values.toString()
	 * 		);
	 * 		System.out.println(result); // 1: [item: 123, item: 156], 5: [item: 567], 2: [item: 233, item: 244]
	 * }</pre>
	 *
	 * @param source 源集合
	 * @param classify 分组方法
	 * @param mapperAfterClassify 分组后value映射方法
	 * @param flatMapper 分组结果映射方法
	 * @param <S> 源集合元素类型
	 * @param <K> 分组后key类型
	 * @param <T> 返回集合元素类型
	 * @param <R> 返回集合元素类型
	 * @return java.util.List<R>
	 * @author huangchengxing
	 * @date 2021/11/3 15:42
	 */
	@NotNull
	public static <S, T, K, R> List<R> groupByThenFlat(
		List<S> source,
		@NotNull Function<S, K> classify,
		@NotNull Function<S, T> mapperAfterClassify,
		@NotNull BiFunction<K, List<T>, R> flatMapper) {

		if (CollectionUtils.isEmpty(source)) {
			return Collections.emptyList();
		}
		Map<K, List<T>> groupByResult = groupByAndThen(source, classify, mapperAfterClassify, (k, l) -> l);
		return toList(groupByResult, flatMapper);
	}


	// ============================ toCollection ============================

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
	@NotNull
	public static <S, T, C extends Collection<T>> C toCollection(
		@NotNull Collection<S> source,
		@NotNull Supplier<C> collFactory,
		@NotNull Function<S, T> mapper) {
		return source.stream()
			.filter(Objects::nonNull)
			.map(mapper)
			.filter(Objects::nonNull)
			.collect(Collectors.toCollection(collFactory));
	}

	/**
	 * 针对源集合中每一个元素创建另一类型元素，经过处理后返回另一类型元素祖册的集合，总是忽略源集合与返回集合中未null的元素 <br />
	 * eg:
	 * <pre>{@code
	 * 		List<String> strings = Arrays.asList("200", "300", null, "500");
	 * 		Set<List<String>> integers = toCollection(
	 * 			strings,
	 * 			HashSet::new, ArrayList::new,
	 * 			(s, i) -> {
	 * 				i.add(s);
	 * 				i.add(s + s);
	 *                        }
	 * 		);
	 * 		System.out.println(integers); // [[300, 300300], [500, 500500], [200, 200200]]
	 * }</pre>
	 *
	 * @param source 源集合
	 * @param collFactory 指定集合的生成方法, 生成的集合不能为null
	 * @param itemFactory 另一类型元素生成方法
	 * @param itemProcessor 源集合与返回集合的处理方法
	 * @param <S> 源集合元素类型
	 * @param <T> 返回集合元素类型
	 * @param <C> 返回集合类型
	 * @return C 转换集合
	 * @author huangchengxing
	 * @date 2021/9/15 10:43
	 */
	@NotNull
	public static <S, T, C extends Collection<T>> C toCollection(
		@NotNull Collection<S> source,
		@NotNull Supplier<C> collFactory,
		@NotNull Supplier<T> itemFactory,
		@NotNull BiConsumer<S, T> itemProcessor) {

		C coll = collFactory.get();
		Objects.requireNonNull(coll);
		if (source.isEmpty()) {
			return coll;
		}

		for (S s : source) {
			T t = itemFactory.get();
			if (Objects.nonNull(s)) {
				itemProcessor.accept(s, t);
				coll.add(t);
			}
		}

		return coll;
	}

	/**
	 * 将一个Map集合转为另一指定Collection集合, 总是过滤转换后的集合中为null的元素 <br />
	 * eg:
	 * <pre>{@code
	 * 		Map<Integer, String> source = MapUtil.<Integer, String> builder()
	 * 			.put(123, "123")
	 * 			.put(233, "123")
	 * 			.put(456, "456")
	 * 			.build();
	 * 		Set<String> result = toCollection(source, HashSet::new, (k, v) -> k.toString() + v);
	 * 		System.out.println(result); // [456456, 123123, 233123]
	 * }</pre>
	 *
	 * @param source 源集合
	 * @param mapper 映射方法
	 * @param collFactory 指定集合的生成方法, 生成的集合不能为null
	 * @param <K> Map集合的key类型
	 * @param <V> Map集合的value类型
	 * @param <T> 返回集合元素类型
	 * @param <C> 返回集合类型
	 * @return C 转换集合
	 * @author huangchengxing
	 * @date 2021/9/15 10:43
	 */
	@NotNull
	public static <K, V, T, C extends Collection<T>> C toCollection(
		Map<K, V> source,
		@NotNull Supplier<C> collFactory,
		@NotNull BiFunction<K, V, T> mapper) {

		if (source == null || source.isEmpty()) {
			C result = collFactory.get();
			Objects.requireNonNull(result);
			return result;
		}
		return source.entrySet().stream()
			.map(e -> mapper.apply(e.getKey(), e.getValue()))
			.filter(Objects::nonNull)
			.collect(Collectors.toCollection(collFactory));
	}



	// ============================ toSet & toList ============================

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
	@NotNull
	public static <S, T> Set<T> toSet(Collection<S> source, @NotNull Function<S, T> mapper) {
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
	@NotNull
	public static <T> Set<T> toSet(Collection<T> source) {
		return toSet(source, Function.identity());
	}

	/**
	 * 将一个集合转为另一HashSet集合
	 *
	 * @param source 源集合
	 * @param mapper 映射方法
	 * @param <K> 源集合key类型
	 * @param <V> 源集合value类型
	 * @param <T> 返回集合元素类型
	 * @return java.util.Set<T> 转换集合
	 * @author huangchengxing
	 * @date 2021/10/19 10:25
	 */
	@NotNull
	public static <K, V, T> Set<T> toSet(Map<K, V> source, @NotNull BiFunction<K, V, T> mapper) {
		return toCollection(source, HashSet::new, mapper);
	}

	/**
	 * 将一个Map集合转为ArrayList集合, 总是过滤转换后的集合中为null的元素
	 *
	 * @param source 源集合
	 * @param mapper 映射方法
	 * @param <K> Map集合的key类型
	 * @param <V> Map集合的value类型
	 * @param <T> 返回集合元素类型
	 * @return java.util.List<T> 转换集合
	 * @author huangchengxing
	 * @date 2021/9/15 10:43
	 */
	@NotNull
	public static <K, V, T> List<T> toList(
		Map<K, V> source,
		@NotNull BiFunction<K, V, T> mapper) {
		return toCollection(source, ArrayList::new, mapper);
	}

	/**
	 * 将一个集合转为另一ArrayList集合
	 *
	 * @param source 源集合
	 * @param mapper 映射方法
	 * @param <S> 源集合元素类型
	 * @param <T> 返回集合元素类型
	 * @return java.util.Set<T> 转换集合
	 * @author huangchengxing
	 * @date 2021/10/19 10:25
	 */
	@NotNull
	public static <S, T> List<T> toList(Collection<S> source, @NotNull Function<S, T> mapper) {
		if (source == null || source.isEmpty()) {
			return Collections.emptyList();
		}
		return toCollection(source, ArrayList::new, mapper);
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
	@NotNull
	public static <T> List<T> toList(Collection<T> source) {
		if (source == null || source.isEmpty()) {
			return Collections.emptyList();
		}
		return toCollection(source, ArrayList::new, Function.identity());
	}

	/**
	 * 针对源集合中每一个元素创建另一类型元素，经过处理后返回另一类型元素的集合，总是忽略源集合与返回集合中未null的元素 <br />
	 * eg:
	 * <pre>{@code
	 * 		List<String> strings = Arrays.asList("200", "300", null, "500");
	 * 		List<List<String>> integers = toList(
	 * 			strings,
	 * 			ArrayList::new,
	 * 			(s, i) -> {
	 * 				i.add(s);
	 * 				i.add(s + s);
	 *                        }
	 * 		);
	 * 		System.out.println(integers); // [[300, 300300], [500, 500500], [200, 200200]]
	 * }</pre>
	 *
	 * @param source 源集合
	 * @param itemFactory 另一类型元素生成方法
	 * @param itemProcessor 源集合与返回集合的处理方法
	 * @param <S> 源集合元素类型
	 * @param <T> 返回集合元素类型
	 * @return C 转换集合
	 * @author huangchengxing
	 * @date 2021/9/15 10:43
	 */
	@NotNull
	public static <S, T> List<T> toList(
		Collection<S> source,
		@NotNull Supplier<T> itemFactory,
		@NotNull BiConsumer<S, T> itemProcessor) {

		return toCollection(source, ArrayList::new, itemFactory, itemProcessor);
	}

	// ============================ filter ============================

	/**
	 * 对集合中非空的元素过滤并将过滤得到的元素作为新集合返回，不修改源集合
	 *
	 * @param source 源集合
	 * @param filter 过滤器
	 * @param <T> 源集合元素类型
	 * @return java.util.List<T> 转换集合
	 * @author huangchengxing
	 * @date 2021/10/19 10:49
	 */
	@NotNull
	public static <T> List<T> filter(Collection<T> source, @NotNull Predicate<T> filter) {
		if (CollectionUtils.isEmpty(source)) {
			return Collections.emptyList();
		}
		return source.stream()
			.filter(Objects::nonNull)
			.filter(filter)
			.collect(Collectors.toList());
	}

	/**
	 * 对集合中非空的元素过滤并将过滤得到的元素作为新集合返回，不修改源集合
	 *
	 * @param source 源集合
	 * @param filter 过滤器
	 * @param <K> 源集合key类型
	 * @param <V> 源集合value类型
	 * @return java.util.Map<K,V> 转换集合
	 * @author huangchengxing
	 * @date 2021/10/19 17:38
	 */
	@NotNull
	public static <K, V> Map<K, V> filter(Map<K, V> source, @NotNull BiPredicate<K, V> filter) {
		if (CollectionUtils.isEmpty(source)) {
			return Collections.emptyMap();
		}
		return source.entrySet().stream()
			.filter(e -> Objects.nonNull(e.getKey()) && Objects.nonNull(e.getValue()))
			.filter(e -> filter.test(e.getKey(), e.getValue()))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	/**
	 * 对集合中非空的元素过滤，返回不为null的元素，不修改源集合
	 *
	 * @param source 源集合
	 * @param <T> 源集合元素类型
	 * @return java.util.List<T> 转换集合
	 * @author huangchengxing
	 * @date 2021/10/19 15:55
	 */
	public static <T> List<T> filterNotNull(Collection<T> source) {
		return filter(source, Objects::nonNull);
	}

	/**
	 * 对集合中非空的元素过滤，并将过滤后的剩余元素作为新集合返回，不修改源集合 <br />
	 * eg:
	 * <pre>{@code
	 * 		Map<Integer, String> source = MapUtil.<Integer, String> builder()
	 * 			.put(123, "123")
	 * 			.put(233, "123")
	 * 			.put(456, "456")
	 * 			.build();
	 * 		Map<Integer, String> result = filterByReverse(source, (k, v) -> !k.toString().equals(v));
	 * 		System.out.println(result); // {456=456, 123=123}
	 * }</pre>
	 *
	 * @param source 源集合
	 * @param filter 过滤器
	 * @param <K> 源集合key类型
	 * @param <V> 源集合value类型
	 * @return java.util.List<T> 转换集合
	 * @author huangchengxing
	 * @date 2021/10/19 10:49
	 */
	@NotNull
	public static <K, V> Map<K, V> filterByReverse(Map<K, V> source, @NotNull BiPredicate<K, V> filter) {
		return filter(source, filter.negate());
	}

	/**
	 * 对集合集合中非空的元素过滤，并将过滤后的剩余元素作为新集合返回，不修改源集合 <br />
	 * eg:
	 * <pre>{@code
	 * 		List<String> source = Arrays.asList("123", "456", "789", "456");
	 * 		List<String> result = filterByReverse(source, s -> s.equals("456"));
	 * 		System.out.println(result); // [123, 789]
	 * }</pre>
	 *
	 * @param source 源集合
	 * @param filter 过滤器
	 * @param <T> 源集合元素类型
	 * @return java.util.List<T> 源集合过滤后剩余元素构成的集合
	 * @author huangchengxing
	 * @date 2021/10/19 10:49
	 */
	@NotNull
	public static <T> List<T> filterByReverse(Collection<T> source, @NotNull Predicate<T> filter) {
		return filter(source, filter.negate());
	}


	// ============================ reduce ============================

	/**
	 * 将集合转为另一类型集合，并对转换后的集合元素进行累加操作，总是过滤转换源集合与转换后的集合中为null的元素 <br />
	 * eg:
	 * <pre>{@code
	 * 		List<String> source = Arrays.asList("200", "300", null, "500");
	 * 		BigDecimal result = reduce(source, BigDecimal::new, BigDecimal.ZERO, BigDecimal::add);
	 * 		System.out.println(result); // 1000
	 * }</pre>
	 *
	 * @param source 源集合
	 * @param mapper 映射方法
	 * @param first 基类元素
	 * @param accumulator 累加器
	 * @return R 累加的元素
	 * @param <T> 源集合元素类型
	 * @param <R> 返回元素类型
	 * @author huangchengxing
	 * @date 2021/10/19 13:30
	 */
	@NotNull
	public static <T, R> R reduce(
		Collection<T> source,
		@NotNull Function<T, R> mapper,
		@NotNull R first,
		@NotNull BinaryOperator<R> accumulator) {

		if (CollectionUtils.isEmpty(source)) {
			return first;
		}
		return source.stream()
			.filter(Objects::nonNull)
			.map(mapper)
			.filter(Objects::nonNull)
			.reduce(first, accumulator);
	}


	/**
	 * 将集合转为另一类型集合，并对转换后的集合元素从第一个非null元素开始进行累加操作，若集合为空或第所有元素为null则返回null。<br />
	 * 总是过滤转换源集合与转换后的集合中为null的元素。<br />
	 * eg:
	 * <pre>{@code
	 * 		List<BigDecimal> source = Arrays.asList(null, new BigDecimal("200"), new BigDecimal("300"), null, new BigDecimal("500"));
	 * 		BigDecimal result = reduce(source, Function.identity(), BigDecimal::add);
	 * 		System.out.println(result); // 1000
	 * }</pre>
	 *
	 * @param source 源集合
	 * @param mapper 映射方法
	 * @param accumulator 累加器
	 * @return R 累加的元素，集合为空或集合中全部元素皆为null时返回null
	 * @param <T> 源集合元素类型
	 * @param <R> 返回元素类型
	 * @author huangchengxing
	 * @date 2021/10/19 13:30
	 */
	public static <T, R> R reduce(
		Collection<T> source,
		@NotNull Function<T, R> mapper,
		@NotNull BinaryOperator<R> accumulator) {

		if (CollectionUtils.isEmpty(source)) {
			return null;
		}

		T first = null;
		long loopStart = 0L;
		for (T t : source) {
			loopStart++;
			if (t != null) {
				first = t;
				break;
			}
		}
		if (first == null) {
			return null;
		}

		return source.stream()
			.skip(loopStart)
			.filter(Objects::nonNull)
			.map(mapper)
			.filter(Objects::nonNull)
			.reduce(mapper.apply(first), accumulator);
	}

	/**
	 * 将集合从第一个开始进行累加操作，若集合为空或第所有元素为null则返回null。<br />
	 * 总是过滤转换源集合与转换后的集合中为null的元素。<br />
	 * eg:
	 * <pre>{@code
	 * 		List<BigDecimal> source = Arrays.asList(null, new BigDecimal("200"), new BigDecimal("300"), null, new BigDecimal("500"));
	 * 		BigDecimal result = reduce(source, BigDecimal::add);
	 * 		System.out.println(result); // 1000
	 * }</pre>
	 *
	 * @param source 源集合
	 * @param accumulator 累加器
	 * @return T 累加的元素，集合为空或集合中全部元素皆为null时返回null
	 * @param <T> 源集合元素类型
	 * @author huangchengxing
	 * @date 2021/10/20 9:42
	 * @see #reduce(Collection, Function, BinaryOperator)
	 */
	public static <T> T reduce(Collection<T> source, @NotNull BinaryOperator<T> accumulator) {
		return reduce(source, Function.identity(), accumulator);
	}

	/**
	 * 对集合元素进行累加操作，总是过滤集合中为null的元素
	 *
	 * @param source 源集合
	 * @param base 基类元素
	 * @param accumulator 累加器
	 * @return R 累加的元素
	 * @param <T> 源集合元素类型
	 * @author huangchengxing
	 * @date 2021/10/19 13:30
	 */
	@NotNull
	public static <T> T reduce(Collection<T> source, @NotNull T base, @NotNull BinaryOperator<T> accumulator) {
		return reduce(source, Function.identity(), base, accumulator);
	}


	// ============================ peek ============================

	/**
	 * 遍历集合元素并进行操作，总是过滤集合中为null的元素
	 *
	 * @param source 源集合
	 * @param action 操作
	 * @return C 源集合，若源集合为null，则返回null
	 * @param <T> 源集合元素类型
	 * @param <C> 源集合类型
	 * @author huangchengxing
	 * @date 2021/10/19 15:10
	 */
	public static <T, C extends Collection<T>> C foreach(C source, @NotNull Consumer<T> action) {
		if (!CollectionUtils.isEmpty(source)) {
			source.stream()
				.filter(Objects::nonNull)
				.forEach(action);
		}
		return source;
	}

	/**
	 * 遍历集合元素并进行操作，总是过滤集合中为key和value任意一者为null的元素
	 *
	 * @param source 源集合
	 * @param action 操作
	 * @return M 源集合，若源集合为null，则返回null
	 * @param <K> 源集合元素类型
	 * @param <V> 源集合类型
	 * @author huangchengxing
	 * @date 2021/10/19 15:10
	 */
	public static <K, V, M extends Map<K, V>> M foreach(M source, @NotNull BiConsumer<K, V> action) {
		if (!CollectionUtils.isEmpty(source)) {
			source.forEach((k, v) -> {
				if (Objects.nonNull(k) && Objects.nonNull(v)) {
					action.accept(k, v);
				}
			});
		}
		return source;
	}


	// ============================ flatMap ============================

	/**
	 * 将一个集合转为二重集合，然后将其平铺为指定类型集合，总是过滤二重集合中的空集合 <br />
	 * eg:
	 * <pre>{@code
	 * 		List<String> source = Arrays.asList("123", "421", null, "233");
	 * 		Set<String> result = flatMap(source, s -> Arrays.asList(s.split("")), HashSet::new);
	 * 		System.out.println(result); // [1, 2, 3, 4]
	 * }</pre>
	 *
	 * @param source 源集合
	 * @param mapper 映射方法
	 * @param collFactory 指定集合的生成方法, 生成的集合不能为null
	 * @return java.util.List<T> 平铺集合
	 * @param <S> 源集合元素类型
	 * @param <T> 平铺集合类型
	 * @param <C> 返回集合类型
	 * @author huangchengxing
	 * @date 2021/10/19 15:10
	 */
	@NotNull
	public static <S, T, C extends Collection<T>> C flatMap(
		Collection<S> source,
		@NotNull Function<S, ? extends Collection<T>> mapper,
		@NotNull Supplier<C> collFactory) {

		if (source == null || source.isEmpty()) {
			C result = collFactory.get();
			Objects.requireNonNull(result);
			return result;
		}
		return source.stream()
			.filter(Objects::nonNull)
			.map(mapper)
			.filter(col -> !CollectionUtils.isEmpty(col))
			.flatMap(Collection::stream)
			.collect(Collectors.toCollection(collFactory));
	}

	/**
	 * 将一个集合转为二重集合，然后将其平铺为ArrayList集合，总是过滤二重集合中的空集合 <br />
	 * eg:
	 * <pre>{@code
	 * 		List<String> source = Arrays.asList("123", "421", null, "233");
	 * 		List<String> result = flatMap(source, s -> Arrays.asList(s.split("")));
	 * 		System.out.println(result); // [1, 2, 3, 4, 2, 1, 2, 3, 3]
	 * }</pre>
	 *
	 * @param source 源集合
	 * @param mapper 映射方法
	 * @return java.util.List<T> 平铺集合
	 * @param <S> 源集合元素类型
	 * @param <T> 平铺集合类型
	 * @author huangchengxing
	 * @date 2021/10/19 15:10
	 */
	@NotNull
	public static <S, T, R extends Collection<T>> List<T> flatMap(Collection<S> source, @NotNull Function<S, R> mapper) {
		return flatMap(source, mapper, ArrayList::new);
	}

	// ============================ join ============================

	/**
	 * 将集合中的非空元素转为字符串，并拼接其中的非空字符串 <br />
	 * eg:
	 * <pre>{@code
	 *     List<Object> objects = Arrays.asList("", null, " ", 123, 456);
	 *     System.out.println(joining(objects, String::valueOf, ", ", true)); // 123, 456
	 *     System.out.println(joining(objects, String::valueOf, ", ", false)); // ,  , 123, 456
	 * }</pre>
	 *
	 * @param source 源集合
	 * @param charMapper 字符串映射方法
	 * @param delimiter 分隔符
	 * @param filterBank 是否过滤空白字符串，包括""与" "
	 * @return java.lang.String
	 * @author huangchengxing
	 * @date 2022/2/16 14:08
	 */
	@NotNull
	public static <T> String joining(
		Collection<T> source, Function<T, ? extends CharSequence> charMapper, CharSequence delimiter, boolean filterBank) {
		if (CollectionUtils.isEmpty(source)) {
			return "";
		}
		if (Objects.isNull(delimiter)) {
			delimiter = "";
		}
		return source.stream()
			.filter(Objects::nonNull)
			.map(charMapper)
			.filter(c -> !filterBank && Objects.nonNull(c) || StringUtils.hasText(c))
			.collect(Collectors.joining(delimiter));
	}

	/**
	 * 将集合中的非空元素转为字符串，并拼接其中的非空白字符串 <br />
	 * eg:
	 * <pre>{@code
	 *     List<Object> objects = Arrays.asList("", null, " ", 123, 456);
	 *     System.out.println(joining(objects, String::valueOf)); // 123456
	 * }</pre>
	 *
	 * @param source 源集合
	 * @param charMapper 字符串映射方法
	 * @return java.lang.String
	 * @author huangchengxing
	 * @date 2022/2/16 14:08
	 */
	@NotNull
	public static <T> String joining(
		Collection<T> source, Function<T, ? extends CharSequence> charMapper) {
		return joining(source, charMapper, "", true);
	}

	// ============================ adapt ============================

	/**
	 * 将Object数据适配为Collection集合
	 * <ul>
	 *     <li>Object: 适配为{@link Collections#singletonList(Object)}</li>
	 *     <li>Map: 获取其values集合；</li>
	 *     <li>Collection: 强转为Collection集合</li>
	 * </ul>
	 *
	 * @param target 目标数据
	 * @return java.util.Collection<?>
	 * @author huangchengxing
	 * @date 2021/12/8 16:26
	 */
	public static Collection<?> adaptToCollection(Object target) {
		if (target instanceof Collection) {
			return (Collection<?>) target;
		} else if (target instanceof Map) {
			return ((Map<?, ?>) target).values();
		} else {
			return Collections.singletonList(target);
		}
	}

	// ============================ private ============================

	/**
	 * 向指定Map集合添加元素，若key已经存在则抛出异常
	 *
	 * @param source 源集合
	 * @param key key
	 * @param value value
	 * @return java.util.Map<K,V>
	 * @exception IllegalStateException 当A或B集合中存在相同的key时抛出
	 * @author huangchengxing
	 * @date 2021/11/4 11:47
	 */
	@NotNull
	private static <K, V> Map<K, V> checkDuplicateAndPut(@NotNull Map<K, V> source, K key, V value)
		throws IllegalStateException {
		return checkDuplicateAndPut(source, key, value, k -> new IllegalStateException("存在重复key：" + key));
	}

	/**
	 * 向源集合添加元素，若key已经存在则抛出异常
	 *
	 * @param source 源集合
	 * @param key key
	 * @param value value
	 * @param <E> 抛出异常
	 * @return java.util.Map<K,V>
	 * @author huangchengxing
	 * @date 2021/11/4 11:47
	 */
	@NotNull
	private static <K, V, E extends Exception> Map<K, V> checkDuplicateAndPut(@NotNull Map<K, V> source, K key, V value, Function<K, E> exceptionFactory)
		throws E {
		if (source.containsKey(key)) {
			throw exceptionFactory.apply(key);
		}
		source.put(key, value);
		return source;
	}

}
