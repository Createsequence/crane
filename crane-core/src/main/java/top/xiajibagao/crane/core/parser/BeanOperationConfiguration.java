package top.xiajibagao.crane.core.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.DisassembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.GlobalConfiguration;
import top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration;

import java.util.List;

/**
 * {@link OperationConfiguration}的通用实现
 *
 * @author huangchengxing
 * @date 2022/03/01 16:05
 */
@Getter
@AllArgsConstructor
public class BeanOperationConfiguration implements OperationConfiguration {

    private final GlobalConfiguration globalConfiguration;
    private final Class<?> targetClass;
    private final List<AssembleOperation> assembleOperations;
    private final List<DisassembleOperation> disassembleOperations;

}
