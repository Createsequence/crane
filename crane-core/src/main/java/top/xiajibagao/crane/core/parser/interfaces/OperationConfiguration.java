package top.xiajibagao.crane.core.parser.interfaces;

import top.xiajibagao.crane.core.operator.interfaces.OperatorFactory;

import java.util.List;

/**
 * 操作配置，用于指定某一个特定类型的全部操作配置
 *
 * @author huangchengxing
 * @date 2022/03/01 14:54
 */
public interface OperationConfiguration {

    /**
     * 获取全局配置
     *
     * @return top.xiajibagao.crane.config.CraneGlobalConfiguration
     * @author huangchengxing
     * @date 2022/3/3 14:09
     */
    GlobalConfiguration getGlobalConfiguration();

    /**
     * 获取配置类型
     *
     * @return java.lang.Class<?>
     * @author huangchengxing
     * @date 2022/3/1 15:02
     */
    Class<?> getTargetClass();

    /**
     * 获取操作者工厂
     *
     * @return top.xiajibagao.crane.operator.interfaces.OperatorFactory
     * @author huangchengxing
     * @date 2022/3/1 15:52
     */
    OperatorFactory getOperatorFactory();

    /**
     * 获取待处理的装配操作
     *
     * @return java.util.List<top.xiajibagao.crane.parse.interfaces.AssembleOperation>
     * @author huangchengxing
     * @date 2022/3/1 15:01
     */
    List<AssembleOperation> getAssembleOperations();

    /**
     * 获取待处理的拆卸操作
     *
     * @return java.util.List<top.xiajibagao.crane.parse.interfaces.DisassembleOperation>
     * @author huangchengxing
     * @date 2022/3/1 15:02
     */
    List<DisassembleOperation> getDisassembleOperations();

}
