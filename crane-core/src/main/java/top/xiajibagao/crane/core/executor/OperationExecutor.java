package top.xiajibagao.crane.core.executor;

import top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration;

/**
 * @author huangchengxing
 * @date 2022/03/01 17:58
 */
public interface OperationExecutor {

    /**
     * 执行操作
     *
     * @param targets 目标实例
     * @param operationConfiguration 目标类操作配置
     * @author huangchengxing
     * @date 2022/3/1 17:59
     */
    void execute(Iterable<?> targets, OperationConfiguration operationConfiguration);

}
