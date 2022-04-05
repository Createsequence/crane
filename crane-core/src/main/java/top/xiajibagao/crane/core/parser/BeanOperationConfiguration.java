package top.xiajibagao.crane.core.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import top.xiajibagao.crane.core.operator.interfaces.OperatorFactory;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.DisassembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.GlobalConfiguration;
import top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration;

import java.util.List;

/**
 * @author huangchengxing
 * @date 2022/03/01 16:05
 */
@Getter
@AllArgsConstructor
public class BeanOperationConfiguration implements OperationConfiguration {

    private final GlobalConfiguration globalConfiguration;
    private final Class<?> targetClass;
    private final OperatorFactory operatorFactory;
    private final List<AssembleOperation> assembleOperations;
    private final List<DisassembleOperation> disassembleOperations;

}
