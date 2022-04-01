package top.xiajibagao.crane.helper;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 两个key对应一个值的map集合
 *
 * @author huangchengxing
 * @date 2022/04/01 20:34
 */
public class BaseTableMap<R, C, V> implements TableMap<R, C, V> {

    private final Map<R, Map<C, V>> rowMap;
    private final Supplier<Map<C, V>> colFactory;

    public BaseTableMap(@NotNull Map<R, Map<C, V>> rowMap, @NotNull Supplier<Map<C, V>> colFactory) {
        this.rowMap = rowMap;
        this.colFactory = colFactory;
    }

    public BaseTableMap() {
        this.rowMap = new HashMap<>();
        this.colFactory = HashMap::new;
    }

    @Override
    public void put(R rowKey, C colKey, V val) {
        Map<C, V> colMap = rowMap.computeIfAbsent(rowKey, rk -> colFactory.get());
        colMap.put(colKey, val);
    }

    @Override
    public V get(R rowKey, C colKey) {
        return ObjectUtils.computeIfNotNull(
            rowMap.get(rowKey),
            colMap -> colMap.get(colKey)
        );
    }

    @Override
    public List<C> colKeys() {
        return rowMap.values().stream()
            .map(Map::keySet)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    @Override
    public List<V> colValues() {
        return rowMap.values().stream()
            .map(Map::values)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    @Override
    public int size() {
        return rowMap.size();
    }

    @Override
    public boolean isEmpty() {
        return rowMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return rowMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return rowMap.containsValue(value);
    }

    @Override
    public Map<C, V> get(Object key) {
        return rowMap.get(key);
    }

    @Override
    public Map<C, V> put(R key, Map<C, V> value) {
        return rowMap.put(key, value);
    }

    @Override
    public Map<C, V> remove(Object key) {
        return rowMap.remove(key);
    }

    @Override
    public void putAll(Map<? extends R, ? extends Map<C, V>> m) {
        rowMap.putAll(m);
    }

    @Override
    public void clear() {
        rowMap.clear();
    }

    @Override
    public Set<R> keySet() {
        return rowMap.keySet();
    }

    @Override
    public Collection<Map<C, V>> values() {
        return rowMap.values();
    }

    @Override
    public Set<Entry<R, Map<C, V>>> entrySet() {
        return rowMap.entrySet();
    }
}
