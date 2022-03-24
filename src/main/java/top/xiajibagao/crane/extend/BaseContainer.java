package top.xiajibagao.crane.extend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import top.xiajibagao.crane.container.Container;
import top.xiajibagao.crane.helper.CollUtils;
import top.xiajibagao.crane.helper.ObjectUtils;
import top.xiajibagao.crane.parse.interfaces.AssembleOperation;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 容器基类，用于提供针对“使用namespace和id获取数据源并操作”的模板代码
 *
 * @param <T> source类型
 * @param <K> key字段类型
 * @author huangchengxing
 * @date 2022/03/23 21:07
 */
@Slf4j
public abstract class BaseContainer<K, T> implements Container {

    @Override
    public void process(List<Object> targets, List<AssembleOperation> operations) {
        if (Objects.isNull(targets) || targets.isEmpty() || CollUtils.isEmpty(operations)) {
            return;
        }
        // 获取key值与命名空间
        MultiValueMap<String, K> namespacesAndKeys = getNamespaceAndKeyFromTargets(targets, operations);
        if (CollUtils.isEmpty(namespacesAndKeys)) {
            return;
        }
        // 根据key值获取数据源
        Map<String, Map<K, T>> sources = ObjectUtils.trySupply(
            () -> getSources(namespacesAndKeys),
            e -> log.warn("容器[{}]获取数据源获取失败，数据[{}]，错误信息：{}", this.getClass(), namespacesAndKeys, e.getMessage())
        );
        if (CollUtils.isEmpty(sources)) {
            return;
        }
        for (Object target : targets) {
            for (AssembleOperation operation : operations) {
                Map<K, ?> keyMap = sources.get(operation.getNamespace());
                if (CollUtils.isEmpty(keyMap)) {
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
        }
    }

    /**
     * 根据数据源id与对应的namespace获取数据集
     *
     * @param namespaceAndKeys namespace-key字段集合
     * @return java.util.Map<java.lang.String,java.util.Map<K, T>> namespace-key-数据源对象集合
     * @author huangchengxing
     * @date 2022/3/21 12:17
     */
    protected abstract Map<String, Map<K, T>> getSources(MultiValueMap<String, K> namespaceAndKeys);

    /**
     * 将对象集合转为所需要的namespace与key集合
     *
     * @param target 对象
     * @param operations 操作配置
     * @return org.springframework.util.MultiValueMap<java.lang.String,K>
     * @author huangchengxing
     * @date 2022/3/21 12:17
     */
    protected MultiValueMap<String, K> getKeyAndNamespaceFromTarget(Object target, List<AssembleOperation> operations) {
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
    protected MultiValueMap<String, K> getNamespaceAndKeyFromTargets(List<Object> targets, List<AssembleOperation> operations) {
        MultiValueMap<String, K> results = new LinkedMultiValueMap<>();
        targets.stream()
            .filter(Objects::nonNull)
            .map(t -> getKeyAndNamespaceFromTarget(t, operations))
            .filter(CollUtils::isNotEmpty)
            .forEach(results::addAll);
        return results;
    }

    /**
     * 将通过操作装配器获取的key转为所需要的类型
     *
     * @param key key
     * @return K
     * @author huangchengxing
     * @date 2022/3/21 12:16
     */
    @SuppressWarnings("unchecked")
    protected K parseKey(Object key) {
        return (K) key;
    }

}
