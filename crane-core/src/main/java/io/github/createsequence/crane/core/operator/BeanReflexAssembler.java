package io.github.createsequence.crane.core.operator;

import cn.hutool.core.collection.CollUtil;
import io.github.createsequence.crane.core.helper.PairEntry;
import io.github.createsequence.crane.core.operator.interfaces.Assembler;
import io.github.createsequence.crane.core.operator.interfaces.OperateProcessor;
import io.github.createsequence.crane.core.parser.BeanPropertyMapping;
import io.github.createsequence.crane.core.parser.EmptyPropertyMapping;
import io.github.createsequence.crane.core.parser.interfaces.AssembleOperation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Objects;

/**
 * 针对普通JavaBean的{@link Assembler}实现，允许基于反射完成对JavaBean的装配操作
 *
 * @author huangchengxing
 * @date 2022/03/02 13:29
 */
@Getter
@RequiredArgsConstructor
public class BeanReflexAssembler implements Assembler {

    private final OperateProcessor operateProcessor;

    @Override
    public void execute(Object target, Object source, AssembleOperation operation) {
        if (Objects.isNull(target) || Objects.isNull(source)) {
            return;
        }
        CollUtil.defaultIfEmpty(operation.getPropertyMappings(), Collections.singletonList(EmptyPropertyMapping.instance()))
            .stream()
            .map(property -> PairEntry.of(property, operateProcessor.readFromSource(source, property, operation)))
            .filter(PairEntry::hasValue)
            .forEach(pair -> operateProcessor.writeToTarget(pair.getValue(), target, pair.getKey(), operation));
    }

    @Override
    public Object getKey(Object target, AssembleOperation operation) {
        return operateProcessor.readFromSource(
            target, BeanPropertyMapping.ofNameOnlyProperty(operation.getTargetProperty().getName()), operation
        );
    }
    
}
