package top.xiajibagao.crane.core.executor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.stream.StreamUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import top.xiajibagao.crane.core.container.Container;
import top.xiajibagao.crane.core.helper.CounterSet;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.DisassembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.GlobalConfiguration;
import top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>有序的{@link OperationExecutor}同步实现。
 *
 * <p>处理时按照每种数据对应的类操作配置分组，因此会按照统一配操作配置中的
 * {@link AssembleOperation#getOrder()}或{@link DisassembleOperation#getOrder()}的大小顺序执行处理。<br />
 * 因此，一次执行中一个相同的容器可能会被访问多次。<br />
 *
 * <b>注意：由于装卸操作总是发生于装配操作前，故无法保证{@link AssembleOperation}与{@link DisassembleOperation}之间的执行顺序</b>
 *
 * @author huangchengxing
 * @date 2022/03/05 14:40
 */
public class SequentialOperationExecutor implements OperationExecutor {

    @Override
    public void execute(Iterable<?> targets, OperationConfiguration configuration) {
        if (CollUtil.isEmpty(targets) || Objects.isNull(configuration)) {
            return;
        }
        List<Object> targetsList = StreamUtil.of(targets).collect(Collectors.toList());

        // 解析配置
        Multimap<OperationConfiguration, Object> collectedConfigurations = collectOperationConfigurations(
            targetsList, configuration, ArrayListMultimap.create()
        );

        execute(configuration.getGlobalConfiguration(), collectedConfigurations);
    }

    protected void execute(@Nonnull GlobalConfiguration globalConfiguration, @Nonnull Multimap<OperationConfiguration, Object> collectedConfigurations) {
        // TODO 优化算法，提高执行效率
        // 获取操作配置，并按类配置分别将全部的操作配置与待处理数据装入桶中，然后对同一桶中的操作按sort排序
        Set<Bucket> buckets = collectedConfigurations.asMap().entrySet()
            .stream()
            .filter(e -> CollUtil.isNotEmpty(e.getKey().getAssembleOperations()))
            .map(e -> new Bucket(e.getKey().getAssembleOperations(), e.getValue()))
            .peek(b -> Collections.sort(b.getOperations()))
            .collect(Collectors.toSet());

        Multimap<Container, Bucket> batch = ArrayListMultimap.create();
        while (CollUtil.isNotEmpty(buckets)) {
            // 找出本轮最匹配的容器
            Container maxContainer = (Container) new CounterSet<>()
                .plusAll(buckets, Bucket::peekContainerOfFirstOperation)
                .getMax();
            // 获取每个桶的队列头符合匹配该容器的操作，直到桶的队列头的操作不匹配该容器为止
            List<Bucket> matchedOperation = buckets.stream()
                .map(b -> b.getOperations(maxContainer))
                .filter(Bucket::isNotEmpty)
                .collect(Collectors.toList());
            batch.putAll(maxContainer, matchedOperation);

            // 移除处理完毕的容器
            buckets = buckets.stream().filter(Bucket::isNotEmpty).collect(Collectors.toSet());
        }

        batch.asMap().forEach(((container, bucketList) -> {
            List<Object> targets = bucketList.stream()
                .map(Bucket::getTargets)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
            List<AssembleOperation> operations = bucketList.stream()
                .map(Bucket::getOperations)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
            Multimap<AssembleOperation, Object> processData = ArrayListMultimap.create();
            operations.forEach(op -> processData.putAll(op, targets));
            container.process(processData);
        }));
    }

    /**
     * 解析数据，将待处理的数据按其对应的类操作配置归类
     *
     * @param targets 当前解析的数据
     * @param configuration 当前解析的数据对应的类操作配置
     * @param collectedConfigurations 已经归类的操作配置
     * @return com.google.common.collect.Multimap<top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration,java.lang.Object>
     * @author huangchengxing
     * @date 2022/3/5 14:58
     */
    @Nonnull
    protected Multimap<OperationConfiguration, Object> collectOperationConfigurations(
        @Nonnull List<Object> targets,
        @Nonnull OperationConfiguration configuration,
        @Nonnull Multimap<OperationConfiguration, Object> collectedConfigurations) {
        // 若无待操作数据则结束解析
        if (CollectionUtils.isEmpty(targets)) {
            return collectedConfigurations;
        }
        targets.forEach(t -> collectedConfigurations.put(configuration, t));

        // 若无嵌套字段则结束解析
        List<DisassembleOperation> disassembleOperations = configuration.getDisassembleOperations();
        if (CollectionUtils.isEmpty(disassembleOperations)) {
            return collectedConfigurations;
        }

        // 若存在嵌套字段递归解析
        for (DisassembleOperation operation : disassembleOperations) {
            List<Object> nestedPropertyValues = targets.stream()
                .map(t -> operation.getDisassembler().execute(t, operation))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
            OperationConfiguration operationConfiguration = operation.getTargetOperateConfiguration();
            collectOperationConfigurations(nestedPropertyValues, operationConfiguration, collectedConfigurations);
        }
        return collectedConfigurations;
    }

    @RequiredArgsConstructor
    @Data
    private static class Bucket {
        private final List<AssembleOperation> operations;
        private final Collection<Object> targets;

        public Container peekContainerOfFirstOperation() {
            return isEmpty() ? null : CollUtil.getFirst(operations).getContainer();
        }

        public Bucket getOperations(Container container) {
            List<AssembleOperation> matched = new ArrayList<>();
            Iterator<AssembleOperation> iterator = operations.iterator();
            while (iterator.hasNext()) {
                AssembleOperation curr = iterator.next();
                if (!Objects.equals(curr.getContainer(), container)) {
                    break;
                }
                matched.add(curr);
                iterator.remove();
            }
            return new Bucket(matched, targets);
        }

        public boolean isEmpty() {
            return CollectionUtils.isEmpty(operations);
        }

        public boolean isNotEmpty() {
            return !isEmpty();
        }

    }

}
