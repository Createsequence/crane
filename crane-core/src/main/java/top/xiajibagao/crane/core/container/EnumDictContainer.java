package top.xiajibagao.crane.core.container;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import lombok.RequiredArgsConstructor;
import top.xiajibagao.crane.core.helper.EnumDict;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * 通过命名空间（枚举名称）与枚举获取唯一值的{@link Container}实现
 *
 * @author huangchengxing
 * @date 2022/03/02 13:20
 */
@RequiredArgsConstructor
public class EnumDictContainer extends BaseNamespaceContainer<String, EnumDict.EnumDictItem<?>> implements Container {

    private final EnumDict enumDict;

    public <T extends Enum<?>> void register(Class<T> targetClass, String typeName, Function<T, String> itemNameGetter) {
        enumDict.register(targetClass, typeName, itemNameGetter);
    }

    public void register(Class<? extends Enum<?>> targetClass) {
        enumDict.register(targetClass);
    }

    /**
     * 注销已注册的枚举
     *
     * @param targetType 目标类型
     * @author huangchengxing
     * @date 2022/1/7 08:05
     * @since 0.5.4
     */
    public <T extends Enum<?>> void unregister(Class<T> targetType) {
        enumDict.unregister(targetType);
    }

    /**
     * 注销已注册的枚举
     *
     * @param targetTypeName 目标类型名称
     * @author huangchengxing
     * @date 2022/1/7 08:05
     * @since 0.5.4
     */
    public void unregister(String targetTypeName) {
        enumDict.unregister(targetTypeName);
    }

    @Nonnull
    @Override
    protected Map<String, Map<String, EnumDict.EnumDictItem<?>>> getSources(@Nonnull Multimap<String, String> namespaceAndKeys) {
        Table<String, String, EnumDict.EnumDictItem<?>> results = HashBasedTable.create();
        namespaceAndKeys.forEach((namespace, key) -> {
            EnumDict.EnumDictItem<?> item = enumDict.getItem(namespace, key);
            if (Objects.nonNull(item)) {
                results.put(namespace, key, item);
            }
        });
        return results.rowMap();
    }

    @Override
    protected String parseKey(@Nullable Object key) {
        return Objects.toString(key);
    }
}
