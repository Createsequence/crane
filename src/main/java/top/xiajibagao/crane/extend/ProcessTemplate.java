package top.xiajibagao.crane.extend;

import top.xiajibagao.crane.operator.interfaces.OperationExecutor;
import top.xiajibagao.crane.operator.interfaces.OperatorFactory;

import java.util.Collection;

/**
 * 数据处理模板，提供整合解析器解析与执行器执行的快速操作
 *
 * @author huangchengxing
 * @date 2022/03/26 13:31
 */
public interface ProcessTemplate {
    
    /**
     * 执行处理
     *
     * @param targets 待处理对象
     * @param operatorFactory 操作者工厂
     * @param executor 执行器
     * @author huangchengxing
     * @date 2022/3/26 13:42
     */
    void process(Collection<?> targets, OperatorFactory operatorFactory, OperationExecutor executor);

    /**
     * 支持处理
     *
     * @param targets 待处理对象
     * @author huangchengxing
     * @date 2022/3/26 13:43
     */
    void process(Collection<?> targets);

}
