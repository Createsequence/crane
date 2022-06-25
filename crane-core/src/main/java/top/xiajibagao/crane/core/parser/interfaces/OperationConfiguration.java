package top.xiajibagao.crane.core.parser.interfaces;

import org.springframework.lang.NonNull;
import top.xiajibagao.crane.core.operator.interfaces.Assembler;
import top.xiajibagao.crane.core.operator.interfaces.Disassembler;

import java.util.List;

/**
 * 操作配置
 *
 * <p>类操作配置，包含了指定类中所有字段所需进行的装卸/装配操作配置，以及进行操作所需的操作者
 * 默认情况下，当操作者不变时，该类应当为单例的。
 *
 * @author huangchengxing
 * @date 2022/03/01 14:54
 * @see AssembleOperation
 * @see DisassembleOperation
 * @see Assembler
 * @see Disassembler
 */
public interface OperationConfiguration {

    /**
     * 获取全局配置
     *
     * @return top.xiajibagao.crane.config.CraneGlobalConfiguration
     * @author huangchengxing
     * @date 2022/3/3 14:09
     */
    @NonNull
    GlobalConfiguration getGlobalConfiguration();

    /**
     * 获取操作配置对应的目标类型
     *
     * @return java.lang.Class<?>
     * @author huangchengxing
     * @date 2022/3/1 15:02
     */
    @NonNull
    Class<?> getTargetClass();

    /**
     * 获取类中所有配置的装配操作
     *
     * @return java.util.List<top.xiajibagao.crane.parse.interfaces.AssembleOperation>
     * @author huangchengxing
     * @date 2022/3/1 15:01
     */
    @NonNull
    List<AssembleOperation> getAssembleOperations();

    /**
     * 获取类中所有配置的装卸操作
     *
     * @return java.util.List<top.xiajibagao.crane.parse.interfaces.DisassembleOperation>
     * @author huangchengxing
     * @date 2022/3/1 15:02
     */
    @NonNull
    List<DisassembleOperation> getDisassembleOperations();

}
