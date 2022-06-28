package top.xiajibagao.crane.core.operator.interfaces;

import top.xiajibagao.crane.core.helper.Orderly;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.PropertyMapping;

import javax.annotation.Nullable;

/**
 * 用于根据配置，将指定数据源写入对象
 *
 * @author huangchengxing
 * @date 2022/06/27 14:05
 * @see 0.5.8
 */
public interface TargetWriter extends OperateProcessorComponent, Orderly {

    /**
     * 是否支持将数据源数据写入待处理对象
     *
     * @param sourceData 从数据源获取的数据
     * @param target 待处理对象
     * @param property 待处理字段
     * @param operation 字段配置
     * @return boolean
     * @author huangchengxing
     * @date 2022/4/8 9:40
     */
    boolean targetCanWrite(@Nullable Object sourceData, @Nullable Object target, PropertyMapping property, AssembleOperation operation);

    /**
     * 将数据源数据写入待处理对象 <br />
     * target必须是{@link #targetCanWrite(Object, Object, PropertyMapping, AssembleOperation)}所支持的类型
     *
     * @param sourceData 从数据源获取的数据
     * @param target 待处理对象
     * @param property 待处理字段
     * @param operation 字段配置
     * @author huangchengxing
     * @date 2022/4/8 9:48
     */
    void writeToTarget(@Nullable Object sourceData, @Nullable Object target, PropertyMapping property, AssembleOperation operation);

}
