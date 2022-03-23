package top.xiajibagao.crane.extend;

import lombok.extern.slf4j.Slf4j;
import top.xiajibagao.crane.container.Container;
import top.xiajibagao.crane.helper.CollUtils;
import top.xiajibagao.crane.helper.ObjectUtils;
import top.xiajibagao.crane.helper.PairEntry;
import top.xiajibagao.crane.parse.interfaces.AssembleOperation;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 容器基类，用于提供针对“使用namespace和id获取数据源并操作”的模板代码
 *
 * @param <K> id字段类型
 * @author huangchengxing
 * @date 2022/03/23 21:07
 */
@Slf4j
public abstract class BaseContainer<K> implements Container {

    @Override
    public void process(List<Object> targets, List<AssembleOperation> operations) {
        if (Objects.isNull(targets) || targets.isEmpty() || CollUtils.isEmpty(operations)) {
            return;
        }
        // 获取key值与命名空间
        List<PairEntry<K, String>> keyEnters = getKeyEntersFromTargets(targets, operations);
        if (CollUtils.isEmpty(keyEnters)) {
            return;
        }
        // 根据key值获取数据源
        Map<String, Map<K, ?>> sources = ObjectUtils.trySupply(
            () -> getSources(keyEnters),
            e -> log.warn("容器[{}]获取数据源获取失败，key[{}]，错误信息：{}", this.getClass(), keyEnters, e.getMessage())
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
     * @param keyEnters id-namespace集合
     * @return java.util.Map<java.lang.String,java.util.Map<K,?>> 命名空间-key值-数据源对象双重集合
     * @author huangchengxing
     * @date 2022/3/21 12:17
     */
    protected abstract Map<String, Map<K, ?>> getSources(List<PairEntry<K, String>> keyEnters);

    /**
     * 从对象中获取所需要的数据源id
     *
     * @param target 对象
     * @param operations 操作配置
     * @return java.util.Set<K>
     * @author huangchengxing
     * @date 2022/3/21 12:17
     */
    protected List<PairEntry<K, String>> getKeyEntersFromTarget(Object target, List<AssembleOperation> operations) {
        List<PairEntry<K, String>> keyEnters = new ArrayList<>();
        for (AssembleOperation operation : operations) {
            Object key = operation.getAssembler().getKey(target, operation);
            K actualKey = parseKey(key);
            if (Objects.nonNull(actualKey)) {
                keyEnters.add(new PairEntry<>(actualKey, operation.getNamespace()));
            }
        }
        return keyEnters;
    }

    /**
     * 将对象集合转为所需要的数据源id
     *
     * @param targets 对象集合
     * @param operations 操作配置
     * @return java.util.Set<K>
     * @author huangchengxing
     * @date 2022/3/21 12:17
     */
    protected List<PairEntry<K, String>> getKeyEntersFromTargets(List<Object> targets, List<AssembleOperation> operations) {
        return targets.stream()
            .filter(Objects::nonNull)
            .map(t -> getKeyEntersFromTarget(t, operations))
            .filter(CollUtils::isNotEmpty)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
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
