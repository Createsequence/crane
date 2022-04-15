package top.xiajibagao.crane.core.container;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.xiajibagao.crane.core.helper.CollUtils;
import top.xiajibagao.crane.core.helper.ObjectUtils;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 容器基类，用于提供针对“使用id获取数据源并操作”的模板代码
 *
 * @param <K> 数据源key类型
 * @author huangchengxing
 * @date 2022/03/21 11:57
 */
@Slf4j
@RequiredArgsConstructor
public abstract class BaseKeyContainer<K> implements Container {

    @Override
    public void process(List<Object> targets, List<AssembleOperation> operations) {
        if (Objects.isNull(targets) || targets.isEmpty()) {
            return;
        }
        // 获取key值
        Set<K> keys = getTargetIds(targets, operations);
        if (CollUtil.isEmpty(keys)) {
            return;
        }
        // 根据key值获取数据源
        Map<K, ?> sources = ObjectUtils.trySupply(
            () -> getSources(keys),
            x -> {
                log.warn("容器[{}]获取数据源获取失败，key[{}]", this.getClass(), keys);
                x.printStackTrace();
            }
        );
        if (CollUtil.isEmpty(sources)) {
            return;
        }
        CollUtils.biForEach(targets, operations, (target, operation) -> writeToTargets(sources, target, operation));
    }

    /**
     * 将数据源写入对象
     */
    protected void writeToTargets(@Nonnull Map<K, ?> sources, @Nullable Object target, @Nonnull AssembleOperation operation) {
        if (Objects.isNull(target)) {
            return;
        }
        Object key = operation.getAssembler().getKey(target, operation);
        Object source = sources.get(parseKey(key));
        if (Objects.nonNull(source)) {
            ObjectUtils.tryAction(
                () -> operation.getAssembler().execute(target, source, operation),
                x -> log.error("字段[{}]处理失败，错误原因：{}", operation.getTargetProperty(), x.getMessage())
            );
        }
    }

    /**
     * 根据数据源id，获取根据id分组的数据源
     *
     * @param keys id集合
     * @return java.util.Map<K, T>
     * @author huangchengxing
     * @date 2022/3/21 12:17
     */
    protected abstract Map<K, Object> getSources(Set<K> keys);

    /**
     * 从对象中获取所需要的数据源id
     *
     * @param target     对象
     * @param operations 操作配置
     * @return java.util.Set<K>
     * @author huangchengxing
     * @date 2022/3/21 12:17
     */
    protected Set<K> getTargetIds(Object target, List<AssembleOperation> operations) {
        return operations.stream()
                .map(operation -> operation.getAssembler().getKey(target, operation))
                .filter(Objects::nonNull)
                .map(this::parseKey)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * 将对象集合转为所需要的数据源id
     *
     * @param targets    对象集合
     * @param operations 操作配置
     * @return java.util.Set<K>
     * @author huangchengxing
     * @date 2022/3/21 12:17
     */
    protected Set<K> getTargetIds(List<Object> targets, List<AssembleOperation> operations) {
        return targets.stream()
                .filter(Objects::nonNull)
                .map(t -> getTargetIds(t, operations))
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
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
