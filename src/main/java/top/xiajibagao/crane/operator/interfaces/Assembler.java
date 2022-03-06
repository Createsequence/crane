package top.xiajibagao.crane.operator.interfaces;

import top.xiajibagao.crane.container.Container;
import top.xiajibagao.crane.parse.interfaces.AssembleOperation;

/**
 * 装配器，用于根据配置从{@link Container}中获取获取原始数据，并根据指定配置将其填充至对象中
 *
 * @author huangchengxing
 * @date 2022/02/28 17:52
 */
public interface Assembler extends Operator {

    /**
     * 根据配置完成装配操作
     *
     * @param target 目标实例
     * @param source 从容器中获取的目标数据源
     * @param operation 操作配置
     * @author huangchengxing
     * @date 2022/2/28 18:56
     */
    void execute(Object target, Object source, AssembleOperation operation);

    /**
     * 从实例中获取所需的key字段数据
     *
     * @param target 目标实例
     * @param operation 操作配置
     * @return java.lang.Object
     * @author huangchengxing
     * @date 2022/2/28 18:18
     */
    Object getKey(Object target, AssembleOperation operation);

}
