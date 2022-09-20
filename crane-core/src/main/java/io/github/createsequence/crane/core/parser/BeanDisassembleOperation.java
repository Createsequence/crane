package io.github.createsequence.crane.core.parser;

import io.github.createsequence.crane.core.operator.interfaces.Disassembler;
import io.github.createsequence.crane.core.parser.interfaces.DisassembleOperation;
import io.github.createsequence.crane.core.parser.interfaces.OperationConfiguration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * {@link DisassembleOperation}的通用实现
 *
 * @author huangchengxing
 * @date 2022/03/01 16:09
 */
@RequiredArgsConstructor
@Getter
public class BeanDisassembleOperation implements DisassembleOperation {

    private final int order;
    private final OperationConfiguration owner;
    private final Disassembler disassembler;
    private final OperationConfiguration targetOperateConfiguration;
    private final Field targetProperty;
    private final Set<String> targetPropertyAliases;

}
