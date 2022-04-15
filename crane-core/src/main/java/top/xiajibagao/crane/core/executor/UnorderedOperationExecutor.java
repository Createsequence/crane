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
 * <p>无序的{@link OperationExecutor}同步实现。
 *
 * <p>处理时按照每个操作的容器分组，因此将不严格按照{@link AssembleOperation#getSort()}或{@link DisassembleOperation#getSort()}
 * 的大小顺序执行处理。<br />
 * 一次执行中，每个容器仅需被访问一次。
 *
 * @author huangchengxing
 * @date 2022/03/01 18:00
 */
public class UnorderedOperationExecutor implements OperationExecutor {

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

    protected void execute(@Nonnull MultiValueMap<Container, PairEntry<AssembleOperation, ?>> pendingOperations) {
        // 按执行器分批待处理进程
        pendingOperations.forEach((container, pairs) -> container.process(
            CollStreamUtil.toList(pairs, PairEntry::getValue), CollStreamUtil.toList(pairs, PairEntry::getKey)
        ));
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

    protected void processAssembleOperations(
        @Nonnull Collection<?> targets,
        @Nonnull OperationConfiguration configuration,
        @Nonnull MultiValueMap<Container, PairEntry<AssembleOperation, ?>> pendingProcessors) {

        List<AssembleOperation> operations = configuration.getAssembleOperations();
        if (CollectionUtils.isEmpty(operations)) {
            return;
        }
        operations.forEach(operation -> pendingProcessors.addAll(
            operation.getContainer(),
            CollStreamUtil.toList(targets, target -> new PairEntry<>(operation, target))
        ));
    }

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
