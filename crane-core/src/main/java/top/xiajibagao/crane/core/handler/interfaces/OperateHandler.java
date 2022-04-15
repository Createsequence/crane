package top.xiajibagao.crane.core.handler.interfaces;

import top.xiajibagao.crane.core.helper.Orderly;
import top.xiajibagao.crane.core.operator.interfaces.Operator;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.AssembleProperty;
import top.xiajibagao.crane.core.parser.interfaces.Operation;

import javax.annotation.Nullable;

/**
 * 操作处理器 <br />
 * 用于在{@link Operator}中从不同类型的数据结构中读取或写入所需数据
 *
 * @author huangchengxing
 * @date 2022/04/06 19:55
 */
public interface OperateHandler extends Orderly {

    /**
     * 是否支持读取数据源
     *
     * @param source 数据源
     * @param property 待处理字段
     * @param operation 字段配置
     * @return boolean
     * @author huangchengxing
     * @date 2022/4/8 9:40
     */
    boolean sourceCanRead(@Nullable Object source, AssembleProperty property, Operation operation);

    /**
     * 是否支持写入对象数据
     *
     * @param sourceData 从数据源获取的数据
     * @param target 待处理对象
     * @param property 待处理字段
     * @param operation 字段配置
     * @return boolean
     * @author huangchengxing
     * @date 2022/4/8 9:40
     */
    boolean targetCanWrite(@Nullable Object sourceData, @Nullable Object target, AssembleProperty property, AssembleOperation operation);

    /**
     * 获取数据源数据 <br />
     * source必须是{@link #sourceCanRead(Object, AssembleProperty, Operation)}所支持的类型
     *
     * @param source 数据源
     * @param property 待处理字段
     * @param operation 字段配置
     * @return java.lang.Object
     * @author huangchengxing
     * @date 2022/4/8 9:48
     */
    Object readFromSource(@Nullable Object source, AssembleProperty property, Operation operation);

    /**
     * 将从数据源获取到的数据写入待处理对象 <br />
     * target必须是{@link #targetCanWrite(Object, Object, AssembleProperty, AssembleOperation)}所支持的类型
     *
     * @param sourceData 从数据源获取的数据
     * @param target 待处理对象
     * @param property 待处理字段
     * @param operation 字段配置
     * @author huangchengxing
     * @date 2022/4/8 9:48
     */
    void writeToTarget(@Nullable Object sourceData, @Nullable Object target, AssembleProperty property, AssembleOperation operation);

}
