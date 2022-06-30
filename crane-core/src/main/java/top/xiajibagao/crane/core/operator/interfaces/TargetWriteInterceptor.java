package top.xiajibagao.crane.core.operator.interfaces;

import top.xiajibagao.crane.core.helper.Orderly;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.PropertyMapping;

import javax.annotation.Nullable;

/**
 * 待处理对象拦截器，在{@link TargetWriter}调用前执行
 *
 * @author huangchengxing
 * @date 2022/06/27 14:11
 * @see 0.6.0
 */
public interface TargetWriteInterceptor extends OperateProcessorComponent, Orderly {

    /**
     * 是否支持拦截写操作的数据源
     *
     * @param sourceData 从数据源获取的数据
     * @param target     待处理对象
     * @param property   待处理字段
     * @param operation  字段配置
     * @return boolean 是否支持拦截写操作的数据源
     * @author huangchengxing
     * @date 2022/4/8 9:48
     */
    boolean supportInterceptSourceWrite(@Nullable Object sourceData, @Nullable Object target, PropertyMapping property, AssembleOperation operation);

    /**
     * 拦截写操作的数据源
     *
     * @param sourceData 从数据源获取的数据
     * @param target     待处理对象
     * @param property   待处理字段
     * @param operation  字段配置
     * @return java.lang.Object 处理后的数据源
     * @author huangchengxing
     * @date 2022/4/8 9:48
     */
    Object interceptSourceWrite(@Nullable Object sourceData, @Nullable Object target, PropertyMapping property, AssembleOperation operation);

}
