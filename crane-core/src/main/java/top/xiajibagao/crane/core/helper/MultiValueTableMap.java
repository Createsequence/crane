package top.xiajibagao.crane.core.helper;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huangchengxing
 * @date 2022/04/18 3:52
 */
public class MultiValueTableMap<R, C, V> {

    private final Map<R, Multimap<C, V>> rowMap;

    public MultiValueTableMap() {
        this.rowMap = new HashMap<>();
    }

    public Multimap<C, V> getColMap(R rowKey) {
        return this.rowMap.get(rowKey);
    }

    public void putVal(R rowKey, C colKey, V val) {
        getOrCreate(rowKey).put(colKey, val);
    }

    public void putValAll(R rowKey, C colKey, Collection<V> val) {
        if (CollUtil.isNotEmpty(val)) {
            Multimap<C, V> colMap = getOrCreate(rowKey);
            val.forEach(v -> colMap.put(colKey, v));
        }
    }

    public Map<R, Multimap<C, V>> asMap() {
        return this.rowMap;
    }

    private Multimap<C, V> getOrCreate(R rowKey) {
        return rowMap.computeIfAbsent(rowKey, rk -> ArrayListMultimap.create());
    }

}
