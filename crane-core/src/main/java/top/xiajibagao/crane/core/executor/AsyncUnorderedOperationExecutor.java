package top.xiajibagao.crane.core.executor;

import cn.hutool.core.collection.CollStreamUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.util.MultiValueMap;
import top.xiajibagao.crane.core.container.Container;
import top.xiajibagao.crane.core.helper.PairEntry;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.DisassembleOperation;

import javax.annotation.Nonnull;
import java.util.List;
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
    protected void execute(@Nonnull MultiValueMap<Container, PairEntry<AssembleOperation, ?>> pendingOperations) {
        int index = 0;
        CompletableFuture<Void>[] tasks = new CompletableFuture[pendingOperations.entrySet().size()];
        for (Map.Entry<Container, List<PairEntry<AssembleOperation, ?>>> entry : pendingOperations.entrySet()) {
            Container container = entry.getKey();
            List<AssembleOperation> operations = CollStreamUtil.toList(entry.getValue(), PairEntry::getKey);
            List<Object> targets = CollStreamUtil.toList(entry.getValue(), PairEntry::getValue);

            tasks[index++] = CompletableFuture.runAsync(
                () -> container.process(targets, operations), executorService
            );
        }
        CompletableFuture.allOf(tasks).join();
    }


}
