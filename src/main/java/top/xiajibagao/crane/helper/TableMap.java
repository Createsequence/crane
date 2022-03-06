package top.xiajibagao.crane.helper;


import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 由两个键确定唯一值的Map
 *
 * @author huangchengxing
 * @date 2022/02/25 17:50
 */
public class TableMap<R, C, V> extends ConcurrentHashMap<R, Map<C, V>> implements Map<R, Map<C, V>> {

    public V put(R rowKey, C colKey, V val) {
        Map<C, V> colMap = computeIfAbsent(rowKey, rk -> new ConcurrentHashMap<>(8));
        return colMap.put(colKey, val);
    }

    public TableMap<R, C, V> putAll(TableMap<R, C, V> target) {
        if (Objects.nonNull(target) && !target.isEmpty()) {
            target.forEach(this::put);
        }
        return target;
    }

    public V get(R rowKey, C colKey) {
        Map<C, V> colMap = get(rowKey);
        return Objects.isNull(colMap) ? null : colMap.get(colKey);
    }

}
