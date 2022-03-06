package top.xiajibagao.crane.parse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.xiajibagao.crane.operator.interfaces.Disassembler;
import top.xiajibagao.crane.parse.interfaces.DisassembleOperation;
import top.xiajibagao.crane.parse.interfaces.OperationConfiguration;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * @author huangchengxing
 * @date 2022/03/01 16:09
 */
@RequiredArgsConstructor
@Getter
public class BeanDisassembleOperation implements DisassembleOperation {

    private final int sort;
    private final OperationConfiguration owner;
    private final Disassembler disassembler;
    private final OperationConfiguration targetOperateConfiguration;
    private final Field targetProperty;
    private final Set<String> targetPropertyAliases;

}
