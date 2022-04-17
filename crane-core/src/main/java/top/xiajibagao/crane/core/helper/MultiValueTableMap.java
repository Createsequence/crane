package top.xiajibagao.crane.core.helper;

import cn.hutool.core.collection.CollUtil;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huangchengxing
 * @date 2022/04/18 3:52
 */
public class MultiValueTableMap<R, C, V> {

    private final Map<R, MultiValueMap<C, V>> rowMap;

    public MultiValueTableMap() {
        this.rowMap = new HashMap<>();
    }

    public MultiValueMap<C, V> getColMap(R rowKey) {
        return this.rowMap.get(rowKey);
    }

    public void putVal(R rowKey, C colKey, V val) {
        getOrCreate(rowKey).add(colKey, val);
    }

    public void putValAll(R rowKey, C colKey, Collection<V> val) {
        if (CollUtil.isNotEmpty(val)) {
            MultiValueMap<C, V> colMap = getOrCreate(rowKey);
            val.forEach(v -> colMap.add(colKey, v));
        }
    }

    public Map<R, MultiValueMap<C, V>> asMap() {
        return this.rowMap;
    }

    private MultiValueMap<C, V> getOrCreate(R rowKey) {
        return rowMap.computeIfAbsent(rowKey, rk -> new LinkedMultiValueMap<>());
    }

}
