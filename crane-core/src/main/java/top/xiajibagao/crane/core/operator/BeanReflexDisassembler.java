package top.xiajibagao.crane.core.operator;

import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ClassUtils;
import top.xiajibagao.crane.core.exception.CraneException;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandlerChain;
import top.xiajibagao.crane.core.helper.CollUtils;
import top.xiajibagao.crane.core.operator.interfaces.Disassembler;
import top.xiajibagao.crane.core.parser.BeanPropertyMapping;
import top.xiajibagao.crane.core.parser.interfaces.DisassembleOperation;

import java.util.*;

/**
 * @author huangchengxing
 * @date 2022/03/02 13:29
 */
@RequiredArgsConstructor
public class BeanReflexDisassembler implements Disassembler {

    private final OperateHandlerChain handlerChain;

    @Override
    public Collection<?> execute(Object target, DisassembleOperation operation) {
        Assert.isFalse(DisassembleOperation.isDynamic(operation));
        List<Object> results = new ArrayList<>();
        // bfs遍历集合
        Object disassemblePropertyValue = handlerChain.readFromSource(
            target, BeanPropertyMapping.ofNameOnlyProperty(operation.getTargetProperty().getName()), operation
        );
        Deque<Object> deque = new LinkedList<>();
        deque.add(disassemblePropertyValue);
        while (!deque.isEmpty()) {
            CollUtils.adaptToCollection(deque.removeFirst())
                .stream().filter(Objects::nonNull)
                .forEach(searchNode -> processNode(searchNode, deque, results, operation));
        }
        return results;
    }

    private void processNode(Object target, Deque<Object> searchQueue, List<Object> resultList, DisassembleOperation operation) {
        if (ClassUtils.isAssignable(Collection.class, target.getClass())) {
            searchQueue.addLast(target);
            return;
        }
        if (ClassUtils.isAssignable(operation.getTargetOperateConfiguration().getTargetClass(), target.getClass())){
            resultList.add(target);
            return;
        }
        CraneException.throwOf(
            "装卸字段[{}]类型不为Collection或指定类型[{}]",
            operation.getTargetProperty(), operation.getTargetOperateConfiguration().getTargetClass()
        );
    }

}
