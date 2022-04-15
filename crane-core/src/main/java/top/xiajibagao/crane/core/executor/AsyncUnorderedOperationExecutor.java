package top.xiajibagao.crane.core.executor;

import cn.hutool.core.collection.CollStreamUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.util.MultiValueMap;
import top.xiajibagao.crane.core.container.Container;
import top.xiajibagao.crane.core.helper.PairEntry;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * {@link UnorderedOperationExecutor}的异步版本
 *
 * @author huangchengxing
 * @date 2022/03/31 19:45
 */
@RequiredArgsConstructor
public class AsyncUnorderedOperationExecutor extends UnorderedOperationExecutor {

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
