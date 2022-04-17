package top.xiajibagao.crane.core.helper;

import cn.hutool.core.collection.CollUtil;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

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
 * @date 2022/04/17 11:04
 */
public interface TableMap<R, C, V> {

    /**
     * 获取val
     *
     * @param rowKey rowKey
     * @param colKey colKey
     * @return V
     * @author huangchengxing
     * @date 2022/4/17 11:13
     */
    default V getVal(R rowKey, C colKey) {
        Map<C, V> colMap = getColMap(rowKey);
        return Objects.isNull(colMap) ? null : colMap.get(colKey);
    }

    /**
     * 获取val，若不存在则返回默认值
     *
     * @param rowKey rowKey
     * @param colKey colKey
     * @param def 默认值
     * @return V
     * @author huangchengxing
     * @date 2022/4/17 11:13
     */
    default V getValOrDefault(R rowKey, C colKey, V def) {
        Map<C, V> colMap = getColMap(rowKey);
        return ObjectUtils.computeIfNotNull(colMap, cm -> cm.get(colKey), def);
    }

    /**
     * 获取ColMap
     *
     * @param rowKey rowKey
     * @return java.util.Map<C,V>
     * @author huangchengxing
     * @date 2022/4/17 11:13
     */
    Map<C, V> getColMap(R rowKey);

    /**
     * 是否存在对应的ColMap
     *
     * @param rowKey rowKey
     * @return boolean
     * @author huangchengxing
     * @date 2022/4/17 11:24
     */
    boolean containsColMap(Object rowKey);

    /**
     * 是否存在对应的val
     *
     * @param rowKey rowKey
     * @param colKey colKey
     * @return boolean
     * @author huangchengxing
     * @date 2022/4/17 11:24
     */
    default boolean containsVal(R rowKey, C colKey) {
        Map<C, V> colMap = getColMap(rowKey);
        return !Objects.isNull(colMap) && colMap.containsKey(colKey);
    }

    /**
     * 添加值
     *
     * @param rowKey rowKey
     * @param colKey colKey
     * @param val val
     * @author huangchengxing
     * @date 2022/4/17 11:13
     */
    void putVal(R rowKey, C colKey, V val);
    
    /**
     * 添加值
     *
     * @param rowKey rowKey
     * @param map map
     * @author huangchengxing
     * @date 2022/4/17 11:18
     */
    default void putVal(R rowKey, Map<C, V> map) {
        if (CollUtil.isNotEmpty(map)) {
            map.forEach((k, v) -> putVal(rowKey, k, v));
        }
    }

    /**
     * 添加ColMap
     *
     * @param map map
     * @author huangchengxing
     * @date 2022/4/17 11:43
     */
    void putColMap(Map<R, Map<C, V>> map);

    /**
     * 获取对应的Map集合
     *
     * @return java.util.Map<R,java.util.Map<C,V>>
     * @author huangchengxing
     * @date 2022/4/17 11:34
     */
    Map<R, Map<C, V>> asMap();

    /**
     * 获取rowKey集合
     *
     * @return java.util.Set<R>
     * @author huangchengxing
     * @date 2022/4/17 11:37
     */
    default Set<R> rowKeySet() {
        return asMap().keySet();
    }

    /**
     * 若val不存在，则获取一个新val并将其加入集合
     *
     * @param rowKey rowKey
     * @param colKey colKey
     * @param compute compute
     * @return V
     * @author huangchengxing
     * @date 2022/4/17 12:19
     */
    default V computeIfAbsent(R rowKey, C colKey, BiFunction<R, C, V> compute) {
        V v = getVal(rowKey, colKey);
        if (Objects.isNull(v)) {
            v = compute.apply(rowKey, colKey);
            putVal(rowKey, colKey, v);
        }
        return v;
    }

    /**
     * 获取val集合
     *
     * @return java.util.Set<R>
     * @author huangchengxing
     * @date 2022/4/17 11:37
     */
    default List<V> values() {
        return asMap().values().stream()
            .map(Map::values)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

}
