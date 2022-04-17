package top.xiajibagao.crane.core.helper;

import cn.hutool.core.collection.CollUtil;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 由两个key确定一个值的Map，可以理解为一个由RowMap和ColMap两个Map组成的二维Map集合，结构如下：
 * <pre>
 *             | colKey1 | colKey2
 *     ----------------------------
 *     rowKey1 |   val   |   val
 *     ----------------------------
 *     rowKey2 |   val   |   val
 *     ----------------------------
 * </pre>
 *
 * @author huangchengxing
 * @date 2022/04/17 11:39
 */
public class BaseTableMap<R, C, V> implements TableMap<R, C, V> {

    private final Supplier<Map<C, V>> colMapFactory;
    private final Map<R, Map<C, V>> rowMap;

    public BaseTableMap(@Nonnull Supplier<Map<R, Map<C, V>>> rowMapFactory, @Nonnull Supplier<Map<C, V>> colMapFactory) {
        this.colMapFactory = colMapFactory;
        this.rowMap = rowMapFactory.get();
    }

    public BaseTableMap(@Nonnull Supplier<Map<C, V>> colMapFactory) {
        this.colMapFactory = colMapFactory;
        this.rowMap = new HashMap<>();
    }

    public BaseTableMap() {
        this(HashMap::new, HashMap::new);
    }

    @Override
    public Map<C, V> getColMap(R rowKey) {
        return this.rowMap.get(rowKey);
    }

    private Map<C, V> getOrCreate(R rowKey) {
        Map<C, V> colMap = rowMap.get(rowKey);
        return Objects.nonNull(colMap) ?
            colMap : rowMap.computeIfAbsent(rowKey, rk -> colMapFactory.get());
    }

    @Override
    public boolean containsColMap(Object rowKey) {
        return this.rowMap.containsKey(rowKey);
    }

    @Override
    public void putVal(R rowKey, C colKey, V val) {
        Map<C, V> colMap = getOrCreate(rowKey);
        colMap.put(colKey, val);
    }

    @Override
    public void putColMap(Map<R, Map<C, V>> map) {
        if (CollUtil.isNotEmpty(map)) {
            rowMap.putAll(map);
        }
    }

    @Override
    public Map<R, Map<C, V>> asMap() {
        return this.rowMap;
    }

}
