package top.xiajibagao.crane.helper;

import java.util.List;
import java.util.Map;

/**
 * 两个key对应一个值的map集合
 *
 * @author huangchengxing
 * @date 2022/04/01 20:27
 */
public interface TableMap<R, C, V> extends Map<R, Map<C, V>> {
    
    /**
     * 添加值
     *
     * @param rowKey rowKey
     * @param colKey colKey
     * @param val val
     * @author huangchengxing
     * @date 2022/4/1 20:29
     */
    void put(R rowKey, C colKey, V val);
    
    /**
     * 获取值
     *
     * @param rowKey rowKey
     * @param colKey colKey
     * @return V
     * @author huangchengxing
     * @date 2022/4/1 20:30
     */
    V get(R rowKey, C colKey);
    
    /**
     * 获取值，若不存在或为null则返回默认值
     *
     * @param rowKey rowKey
     * @param colKey colKey
     * @param def 默认值
     * @return V
     * @author huangchengxing
     * @date 2022/4/1 20:30
     */
    default V getOrDefault(R rowKey, C colKey, V def) {
        return ObjectUtils.computeIfNotNull(
            get(rowKey),
            colMap -> colMap.get(colKey),
            def
        );
    }

    /**
     * 获取全部的colMap的key集合
     *
     * @return java.util.List<C>
     * @author huangchengxing
     * @date 2022/4/1 20:30
     */
    List<C> colKeys();
    
    /**
     * 获取全部的colMap的value集合
     *
     * @return java.util.List<V>
     * @author huangchengxing
     * @date 2022/4/1 20:30
     */
    List<V> colValues();

    /**
     * 遍历集合
     *
     * @param consumer consumer
     * @author huangchengxing
     * @date 2022/4/1 20:33
     */
    default void forEach(ThiConsumer<R, C, V> consumer) {
        forEach((r, cm) -> cm.forEach((c, m) -> consumer.accept(r, c, m)));
    }

}
