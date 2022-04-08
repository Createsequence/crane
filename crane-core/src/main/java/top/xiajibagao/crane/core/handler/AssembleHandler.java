package top.xiajibagao.crane.core.handler;

import top.xiajibagao.crane.core.operator.interfaces.Assembler;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.AssembleProperty;

/**
 * 装配处理器 <br />
 * 用于在{@link Assembler}中用于从不同类型的数据源中根据配置获取所需要的数据，
 * 并将数据填充到不同类型的对象中。
 *
 * @author huangchengxing
 * @date 2022/04/06 19:55
 */
public interface AssembleHandler {

    /**
     * 排序，越小越靠前
     *
     * @return int
     * @author huangchengxing
     * @date 2022/4/8 20:30
     */
    default int order() {
        return 0;
    }

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
    boolean sourceCanRead(Object source, AssembleProperty property, AssembleOperation operation);

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
    boolean targetCanWrite(Object sourceData, Object target, AssembleProperty property, AssembleOperation operation);

    /**
     * 获取数据源数据 <br />
     * source必须是{@link #sourceCanRead(Object, AssembleProperty, AssembleOperation)}所支持的类型
     *
     * @param source 数据源
     * @param property 待处理字段
     * @param operation 字段配置
     * @return java.lang.Object
     * @author huangchengxing
     * @date 2022/4/8 9:48
     */
    Object readFromSource(Object source, AssembleProperty property, AssembleOperation operation);

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
    void writeToTarget(Object sourceData, Object target, AssembleProperty property, AssembleOperation operation);

}
