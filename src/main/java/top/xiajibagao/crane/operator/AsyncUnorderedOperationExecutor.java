package top.xiajibagao.crane.operator;

import lombok.RequiredArgsConstructor;
import org.springframework.util.MultiValueMap;
import top.xiajibagao.crane.container.Container;
import top.xiajibagao.crane.helper.CollUtils;
import top.xiajibagao.crane.helper.PairEntry;
import top.xiajibagao.crane.parse.interfaces.AssembleOperation;

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

    @Override
    protected void execute(MultiValueMap<Container, PairEntry<AssembleOperation, ?>> pendingOperations) {
        int index = 0;
        CompletableFuture<Void>[] tasks = new CompletableFuture[pendingOperations.entrySet().size()];
        for (Map.Entry<Container, List<PairEntry<AssembleOperation, ?>>> entry : pendingOperations.entrySet()) {
            Container container = entry.getKey();
            List<AssembleOperation> operations = CollUtils.toList(entry.getValue(), PairEntry::getKey);
            List<Object> targets = CollUtils.toList(entry.getValue(), PairEntry::getValue);

            tasks[index++] = CompletableFuture.runAsync(
                () -> container.process(targets, operations), executorService
            );
        }
        CompletableFuture.allOf(tasks).join();
    }


}
