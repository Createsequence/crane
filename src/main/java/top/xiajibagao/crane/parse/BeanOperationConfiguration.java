package top.xiajibagao.crane.parse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import top.xiajibagao.crane.config.CraneGlobalConfiguration;
import top.xiajibagao.crane.operator.interfaces.OperatorFactory;
import top.xiajibagao.crane.parse.interfaces.AssembleOperation;
import top.xiajibagao.crane.parse.interfaces.DisassembleOperation;
import top.xiajibagao.crane.parse.interfaces.OperationConfiguration;

import java.util.List;

/**
 * @author huangchengxing
 * @date 2022/03/01 16:05
 */
@Getter
@AllArgsConstructor
public class BeanOperationConfiguration implements OperationConfiguration {

    private final CraneGlobalConfiguration globalConfiguration;
    private final Class<?> targetClass;
    private final OperatorFactory operatorFactory;
    private final List<AssembleOperation> assembleOperations;
    private final List<DisassembleOperation> disassembleOperations;

}
