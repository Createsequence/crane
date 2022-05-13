package top.xiajibagao.crane.core.container;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

/**
 * 通过命名空间与键值获取唯一值的{@link Container}实现
 *
 * @author huangchengxing
 * @date 2022/03/02 13:19
 */
public class KeyValueContainer extends BaseNamespaceContainer<String, Object> implements Container {

    /**
     * 数据缓存
     */
    public final Table<String, String, Object> cache = HashBasedTable.create();

    /**
     * 注册值
     *
     * @param namespace 命名空间
     * @param values 要添加缓存
     * @author huangchengxing
     * @date 2022/2/25 14:57
     */
    public void register(String namespace, Map<String, ?> values) {
        if (CollectionUtils.isEmpty(values)) {
            return;
        }
        values.forEach((k, v) -> cache.put(namespace, k, v));
    }

    /**
     * 获取值
     *
     * @param namespace 命名空间
     * @param key key
     * @return T
     * @author huangchengxing
     * @date 2022/2/25 15:01
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String namespace, String key) {
        return (T) cache.get(namespace, key);
    }

    @Nonnull
    @Override
    protected Map<String, Map<String, Object>> getSources(@Nonnull Multimap<String, String> namespaceAndKeys) {
        return cache.rowMap();
    }

    @Override
    protected String parseKey(@Nullable Object key) {
        return Objects.toString(key);
    }
}
