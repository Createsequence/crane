package top.xiajibagao.crane.core.container;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import top.xiajibagao.crane.core.helper.CollUtils;
import top.xiajibagao.crane.core.helper.ObjectUtils;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 容器基类，用于提供针对“使用namespace和key获取数据源并操作”的模板代码
 *
 * @param <T> 数据源对象类型
 * @param <K> key字段类型
 * @author huangchengxing
 * @date 2022/03/23 21:07
 */
@Slf4j
public abstract class BaseNamespaceContainer<K, T> implements Container {

    @Override
    public void process(List<Object> targets, List<AssembleOperation> operations) {
        if (CollUtil.isEmpty(targets) || CollUtil.isEmpty(operations)) {
            return;
        }
        // 获取key值与命名空间
        MultiValueMap<String, K> namespacesAndKeys = getNamespaceAndKeyFromTargets(targets, operations);
        if (CollUtil.isEmpty(namespacesAndKeys)) {
            return;
        }
        // 根据key值获取数据源
        Map<String, Map<K, T>> sources = ObjectUtils.trySupply(
            () -> getSources(namespacesAndKeys),
            e -> log.warn("容器[{}]获取数据源获取失败，数据[{}]，错误信息：{}", this.getClass(), namespacesAndKeys, e.getMessage())
        );
        if (CollUtil.isEmpty(sources)) {
            return;
        }
        CollUtils.biForEach(targets, operations, (target, operation) -> writeToTargets(sources, target, operation));
    }

    /**
     * 将数据源写入对象
     *
     * @param sources 数据源
     * @param target 待处理对象
     * @param operation 装配配置
     * @author huangchengxing
     * @date 2022/4/18 0:16
     */
    protected void writeToTargets(@Nonnull Map<String, Map<K, T>> sources, @CheckForNull Object target, @Nonnull AssembleOperation operation) {
        if (Objects.isNull(target)) {
            return;
        }
        Map<K, ?> keyMap = sources.get(operation.getNamespace());
        if (CollUtil.isEmpty(keyMap)) {
            return;
        }
        Object key = operation.getAssembler().getKey(target, operation);
        Object source = keyMap.get(parseKey(key));
        if (Objects.isNull(source)) {
            return;
        }
        ObjectUtils.tryAction(
            () -> operation.getAssembler().execute(target, source, operation),
            x -> log.error("字段[{}]处理失败，错误原因：{}", operation.getTargetProperty(), x.getMessage())
        );
    }

    /**
     * 根据装配的key字段值与namespace获取对应的数据源对象集合
     *
     * @param namespaceAndKeys namespace与key字段值集合
     * @return java.util.Map<K, T>
     * @author huangchengxing
     * @date 2022/3/21 12:17
     */
    @Nonnull
    protected abstract Map<String, Map<K, T>> getSources(@Nonnull MultiValueMap<String, K> namespaceAndKeys);

    /**
     * 将对象集合转为所需要的namespace与key集合
     *
     * @param target 对象
     * @param operations 操作配置
     * @return org.springframework.util.MultiValueMap<java.lang.String,K>
     * @author huangchengxing
     * @date 2022/3/21 12:17
     */
    @Nonnull
    protected MultiValueMap<String, K> getKeyAndNamespaceFromTarget(@Nonnull Object target, @Nonnull List<AssembleOperation> operations) {
        MultiValueMap<String, K> results = new LinkedMultiValueMap<>();
        for (AssembleOperation operation : operations) {
            Object key = operation.getAssembler().getKey(target, operation);
            K actualKey = parseKey(key);
            if (Objects.nonNull(actualKey)) {
                results.add(operation.getNamespace(), actualKey);
            }
        }
        return results;
    }

    /**
     * 将对象集合转为所需要的namespace与key集合
     *
     * @param targets 对象集合
     * @param operations 操作配置
     * @return org.springframework.util.MultiValueMap<java.lang.String,K>
     * @author huangchengxing
     * @date 2022/3/21 12:17
     */
    @Nonnull
    protected MultiValueMap<String, K> getNamespaceAndKeyFromTargets(@Nonnull List<Object> targets, @Nonnull List<AssembleOperation> operations) {
        MultiValueMap<String, K> results = new LinkedMultiValueMap<>();
        targets.stream()
            .filter(Objects::nonNull)
            .map(t -> getKeyAndNamespaceFromTarget(t, operations))
            .filter(CollUtil::isNotEmpty)
            .forEach(results::addAll);
        return results;
    }

    /**
     * 将获取的key字段值转为所需要的类型
     *
     * @param key key
     * @return K
     * @author huangchengxing
     * @date 2022/3/21 12:16
     */
    @Nullable
    @SuppressWarnings("unchecked")
    protected K parseKey(@Nullable Object key) {
        return (K) key;
    }

}
