package io.github.createsequence.crane.core.operator;

import cn.hutool.core.lang.Assert;
import io.github.createsequence.crane.core.exception.CraneException;
import io.github.createsequence.crane.core.helper.CollUtils;
import io.github.createsequence.crane.core.operator.interfaces.Disassembler;
import io.github.createsequence.crane.core.operator.interfaces.OperateProcessor;
import io.github.createsequence.crane.core.parser.BeanPropertyMapping;
import io.github.createsequence.crane.core.parser.interfaces.DisassembleOperation;
import io.github.createsequence.crane.core.parser.interfaces.DynamicDisassembleOperation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ClassUtils;

import java.util.*;

/**
 * 针对普通JavaBean的{@link Disassembler}实现，允许基于反射完成对JavaBean的装卸操作
 *
 * @author huangchengxing
 * @date 2022/03/02 13:29
 */
@Getter
@RequiredArgsConstructor
public class BeanReflexDisassembler implements Disassembler {

    private final OperateProcessor operateProcessor;

    @Override
    public Collection<?> execute(Object target, DisassembleOperation operation) {
        Assert.isFalse(DisassembleOperation.isDynamic(operation), "无法处理{}", DynamicDisassembleOperation.class);
        List<Object> results = new ArrayList<>();
        // bfs遍历集合
        Object disassemblePropertyValue = operateProcessor.readFromSource(
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
