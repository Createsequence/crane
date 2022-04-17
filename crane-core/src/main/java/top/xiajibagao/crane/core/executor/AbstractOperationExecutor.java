package top.xiajibagao.crane.core.executor;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import top.xiajibagao.crane.core.container.Container;
import top.xiajibagao.crane.core.helper.PairEntry;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.DisassembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 抽象操作执行器
 * <p>{@link OperationExecutor}初步实现，提供基本的装配与装卸操作的收集处理。
 * 实现类必须实现{@link #execute(MultiValueMap)}方法
 *
 * @author huangchengxing
 * @date 2022/04/17 20:36
 */
public abstract class AbstractOperationExecutor implements OperationExecutor {

    @Override
    public void execute(Iterable<?> targets, OperationConfiguration configuration) {
        if (CollUtil.isEmpty(targets) || Objects.isNull(configuration)) {
            return;
        }
        List<?> targetsList = StreamSupport.stream(targets.spliterator(), false)
            .collect(Collectors.toList());
        // 分组收集待处理的进程
        MultiValueMap<Container, PairEntry<AssembleOperation, ?>> pendingOperations = new LinkedMultiValueMap<>();
        collectOperations(targetsList, configuration, pendingOperations);
        // 执行
        execute(pendingOperations);
    }

    private void collectOperations(
        Collection<?> targets,
        OperationConfiguration configuration,
        MultiValueMap<Container, PairEntry<AssembleOperation, ?>> pendingOperations) {

        if (CollectionUtils.isEmpty(targets)) {
            return;
        }
        // 将普通字段添加到待处理
        processAssembleOperations(targets, configuration, pendingOperations);
        // 处理嵌套字段
        processDisassembleOperations(targets, configuration, pendingOperations);
    }

    /**
     * 执行操作
     *
     * @param pendingOperations 待执行的操作
     * @author huangchengxing
     * @date 2022/4/17 20:37
     */
    protected abstract void execute(@Nonnull MultiValueMap<Container, PairEntry<AssembleOperation, ?>> pendingOperations);

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
        @Nonnull Collection<?> targets,
        @Nonnull OperationConfiguration configuration,
        @Nonnull MultiValueMap<Container, PairEntry<AssembleOperation, ?>> pendingOperations) {

        List<AssembleOperation> operations = configuration.getAssembleOperations();
        if (CollectionUtils.isEmpty(operations)) {
            return;
        }
        operations.forEach(operation -> pendingOperations.addAll(
            operation.getContainer(),
            CollStreamUtil.toList(targets, target -> new PairEntry<>(operation, target))
        ));
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
        @Nonnull MultiValueMap<Container, PairEntry<AssembleOperation, ?>> pendingOperations) {

        List<DisassembleOperation> disassembleOperations = configuration.getDisassembleOperations();
        if (CollectionUtils.isEmpty(disassembleOperations)) {
            return;
        }
        for (DisassembleOperation operation : disassembleOperations) {
            // 将嵌套字段取出平铺
            Collection<?> nestedPropertyValues = targets.stream()
                .map(t -> operation.getDisassembler().execute(t, operation))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
            OperationConfiguration operationConfiguration = operation.getTargetOperateConfiguration();
            // 递归解析嵌套对象
            collectOperations(nestedPropertyValues, operationConfiguration, pendingOperations);
        }
    }

}
