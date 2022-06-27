package top.xiajibagao.crane.core.operator.interfaces;

import top.xiajibagao.crane.core.helper.Orderly;
import top.xiajibagao.crane.core.parser.interfaces.Operation;
import top.xiajibagao.crane.core.parser.interfaces.PropertyMapping;

import javax.annotation.Nullable;

/**
 * 数据源拦截器，在{@link SourceReader}调用前执行
 *
 * @author huangchengxing
 * @date 2022/06/27 14:10
 * @since 0.5.8
 */
public interface SourceReadInterceptor extends GroupRegistrable, Orderly {

    /**
     * 是否支持拦截读操作的数据源
     *
     * @param source    数据源
     * @param property  待处理字段
     * @param operation 字段配置
     * @return boolean 是否支持拦截读操作的数据源
     * @author huangchengxing
     * @date 2022/4/8 9:48
     */
    boolean supportInterceptReadSource(@Nullable Object source, PropertyMapping property, Operation operation);

    /**
     * 拦截读操作的数据源
     *
     * @param source    数据源
     * @param property  待处理字段
     * @param operation 字段配置
     * @return java.lang.Object 处理后的数据源数据
     * @author huangchengxing
     * @date 2022/4/8 9:48
     */
    Object interceptReadSource(@Nullable Object source, PropertyMapping property, Operation operation);

}
