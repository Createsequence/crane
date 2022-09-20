package io.github.createsequence.crane.core.executor;

import cn.hutool.core.collection.CollUtil;
import io.github.createsequence.crane.core.container.Container;
import io.github.createsequence.crane.core.helper.MultiValueTableMap;
import io.github.createsequence.crane.core.parser.interfaces.AssembleOperation;
import io.github.createsequence.crane.core.parser.interfaces.DisassembleOperation;
import io.github.createsequence.crane.core.parser.interfaces.GlobalConfiguration;
import io.github.createsequence.crane.core.parser.interfaces.OperationConfiguration;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 抽象操作执行器
 *
 * <p>{@link OperationExecutor}初步实现，提供基本的装配与装卸操作的收集处理。
 * 实现类必须实现{@link #execute(GlobalConfiguration, MultiValueTableMap)}方法
 *
 * @author huangchengxing
 * @date 2022/04/17 20:36
 */
public abstract class AbstractOperationExecutor implements OperationExecutor {

    @Override
    public void execute(Iterable<?> targets, OperationConfiguration configuration, @Nonnull Set<Class<?>> groups) {
        if (CollUtil.isEmpty(targets) || Objects.isNull(configuration) || CollUtil.isEmpty(groups)) {
            return;
        }
        List<Object> targetsList = StreamSupport.stream(targets.spliterator(), false)
            .collect(Collectors.toList());
        // 分组收集待进行的操作配置
        MultiValueTableMap<Container, AssembleOperation, Object> pendingOperations = new MultiValueTableMap<>();
        collectOperations(targetsList, configuration, groups, pendingOperations);
        // 执行
        execute(configuration.getGlobalConfiguration(), pendingOperations);
    }

    private void collectOperations(
        Collection<Object> targets,
        OperationConfiguration configuration,
        Set<Class<?>> targetGroups,
        MultiValueTableMap<Container, AssembleOperation, Object> pendingOperations) {
        if (CollectionUtils.isEmpty(targets)) {
            return;
        }
        // 处理普通待装配字段
        processAssembleOperations(targets, configuration, targetGroups, pendingOperations);
        // 处理待装卸的嵌套字段
        processDisassembleOperations(targets, configuration, targetGroups, pendingOperations);
    }

    /**
     * 执行操作
     *
     * @param globalConfiguration 全局配置
     * @param pendingOperations 待执行的操作
     * @author huangchengxing
     * @date 2022/4/17 20:37
     */
    protected abstract void execute(@Nonnull GlobalConfiguration globalConfiguration, @Nonnull MultiValueTableMap<Container, AssembleOperation, Object> pendingOperations);

    /**
     * 处理装配操作
     *
     * @param targets 待处理对象
     * @param configuration 配置类型
     * @param pendingOperations 待执行操作
     * @author huangchengxing
     * @date 2022/4/17 20:38
     */
    protected void processAssembleOperations(
        @Nonnull Collection<Object> targets,
        @Nonnull OperationConfiguration configuration, Set<Class<?>> targetGroups,
        @Nonnull MultiValueTableMap<Container, AssembleOperation, Object> pendingOperations) {

        List<AssembleOperation> operations = configuration.getAssembleOperations();
        if (CollectionUtils.isEmpty(operations)) {
            return;
        }
        operations.stream()
            .filter(op -> CollUtil.containsAny(targetGroups, op.getGroups()))
            .forEach(op -> pendingOperations.putValAll(op.getContainer(), op, targets));
    }

    /**
     * 处理装卸操作
     *
     * @param targets 待处理对象
     * @param configuration 配置类型
     * @param pendingOperations 待执行操作
     * @author huangchengxing
     * @date 2022/4/17 20:37
     */
    protected void processDisassembleOperations(
        @Nonnull Collection<?> targets,
        @Nonnull OperationConfiguration configuration,
        Set<Class<?>> targetGroups,
        @Nonnull MultiValueTableMap<Container, AssembleOperation, Object> pendingOperations) {

        List<DisassembleOperation> disassembleOperations = configuration.getDisassembleOperations();
        if (CollectionUtils.isEmpty(disassembleOperations)) {
            return;
        }
        for (DisassembleOperation operation : disassembleOperations) {
            DisassembleOperation.collect(operation, targets).asMap()
                .forEach((config, values) -> collectOperations(values, config, targetGroups, pendingOperations));
        }
    }

}
