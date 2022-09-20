package io.github.createsequence.crane.core.operator.interfaces;

import io.github.createsequence.crane.core.parser.interfaces.AssembleOperation;

/**
 * 装配器。用于根据指定的装配配置，获取对应的key字段值，或将数据源中的字段写入待操作对象
 *
 * @see AssembleOperation
 * @author huangchengxing
 * @date 2022/02/28 17:52
 */
public interface Assembler extends Operator {

    /**
     * 根据装配操作配置，将数据源写入待处理对象
     *
     * @param target 待处理对象
     * @param source 数据源
     * @param operation 操作配置
     * @author huangchengxing
     * @date 2022/2/28 18:56
     */
    void execute(Object target, Object source, AssembleOperation operation);

    /**
     * 从待处理对象中获取操作配置对应的key字段值
     *
     * @param target 待处理对象
     * @param operation 操作配置
     * @return java.lang.Object
     * @author huangchengxing
     * @date 2022/2/28 18:18
     */
    Object getKey(Object target, AssembleOperation operation);

}
