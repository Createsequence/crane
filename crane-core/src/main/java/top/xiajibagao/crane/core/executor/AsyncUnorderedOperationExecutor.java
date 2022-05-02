package top.xiajibagao.crane.core.executor;

import lombok.RequiredArgsConstructor;
import top.xiajibagao.crane.core.container.Container;
import top.xiajibagao.crane.core.helper.MultiValueTableMap;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.DisassembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.GlobalConfiguration;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * {@link OperationExecutor}异步无序实现
 *
 * <p>处理时按照每个操作的容器分组，因此将不严格按照{@link AssembleOperation#getOrder()}或{@link DisassembleOperation#getOrder()}
 * 的大小顺序执行处理。<br />
 * 一次执行中，每个容器仅需被访问一次。
 *
 * @author huangchengxing
 * @date 2022/03/31 19:45
 */
@RequiredArgsConstructor
public class AsyncUnorderedOperationExecutor extends AbstractOperationExecutor {

    private final ExecutorService executorService;

    @SuppressWarnings("unchecked")
    @Override
    protected void execute(@Nonnull GlobalConfiguration globalConfiguration, @Nonnull MultiValueTableMap<Container, AssembleOperation, Object> pendingOperations) {
        CompletableFuture<Void>[] sortedTasks = pendingOperations.asMap().entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .map(e -> CompletableFuture.runAsync(() -> e.getKey().process(e. getValue()), executorService))
            .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(sortedTasks).join();
    }


}
