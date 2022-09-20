package io.github.createsequence.crane.core.helper;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ObjIntConsumer;

/**
 * 基于HashMap的计数器实现
 *
 * @author huangchengxing
 * @date 2022/03/06 13:26
 */
public class CounterSet<T> {

    private final Map<T, Integer> counter = new HashMap<>();

    public Set<T> keySet() {
        return counter.keySet();
    }

    public <K> CounterSet<T> plusAll(Collection<K> targets, Function<K, T> mapping) {
        targets.stream()
            .filter(Objects::nonNull)
            .forEach(t -> plus(mapping.apply(t)));
        return this;
    }

    public CounterSet<T> plus(T target) {
        updateCount(target, count -> count + 1);
        return this;
    }

    public CounterSet<T> sub(T target) {
        updateCount(target, count -> count - 1);
        return this;
    }

    private void updateCount(T target, IntFunction<Integer> update) {
        Integer count = counter.get(target);
        Integer nextCount = ObjectUtils.computeIfNotNull(count, update::apply, -1);
        counter.put(target, nextCount);
    }

    public T getMax() {
        return counter.entrySet()
            .stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }

    public T getMin() {
        return counter.entrySet()
            .stream()
            .min(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }

    public void foreachByAsc(ObjIntConsumer<T> consumer) {
        counter.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .forEach(e -> consumer.accept(e.getKey(), e.getValue()));
    }

    public void foreachByDesc(ObjIntConsumer<T> consumer) {
        counter.entrySet()
            .stream()
            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
            .forEach(e -> consumer.accept(e.getKey(), e.getValue()));
    }

}
