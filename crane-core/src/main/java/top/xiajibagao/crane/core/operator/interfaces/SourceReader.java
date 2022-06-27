package top.xiajibagao.crane.core.operator.interfaces;

import top.xiajibagao.crane.core.helper.Orderly;
import top.xiajibagao.crane.core.parser.interfaces.Operation;
import top.xiajibagao.crane.core.parser.interfaces.PropertyMapping;

import javax.annotation.Nullable;

/**
 * 用于根据配置，从指定数据源中读取数据
 *
 * @author huangchengxing
 * @date 2022/06/27 14:05
 * @see 0.5.8
 */
public interface SourceReader extends GroupRegistrable, Orderly {

    /**
     * 是否支持从数据源中读取数据
     *
     * @param source 数据源
     * @param property 待处理字段
     * @param operation 字段配置
     * @return boolean
     * @author huangchengxing
     * @date 2022/4/8 9:40
     */
    boolean sourceCanRead(@Nullable Object source, PropertyMapping property, Operation operation);

    /**
     * 从数据源中读取数据 <br />
     * source必须是{@link #sourceCanRead(Object, PropertyMapping, Operation)}所支持的类型
     *
     * @param source 数据源
     * @param property 待处理字段
     * @param operation 字段配置
     * @return java.lang.Object
     * @author huangchengxing
     * @date 2022/4/8 9:48
     */
    @Nullable
    Object readFromSource(@Nullable Object source, PropertyMapping property, Operation operation);

}
