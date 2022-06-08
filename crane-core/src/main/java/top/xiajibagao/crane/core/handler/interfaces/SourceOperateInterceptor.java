package top.xiajibagao.crane.core.handler.interfaces;

import top.xiajibagao.crane.core.handler.ExpressionPreprocessingInterceptor;
import top.xiajibagao.crane.core.helper.Orderly;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.Operation;
import top.xiajibagao.crane.core.parser.interfaces.PropertyMapping;

import javax.annotation.Nullable;

/**
 * 数据源操作拦截器
 * <p>数据源拦截器，用于在数据源被操作处理器链链读取或写入待处理对象前，对数据源进行拦截或改写操作
 *
 * @see ExpressionPreprocessingInterceptor
 * @author huangchengxing
 * @date 2022/06/04 22:55
 * @since 0.5.5
 */
public interface SourceOperateInterceptor extends Orderly {

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
    default boolean supportInterceptReadSource(@Nullable Object source, PropertyMapping property, Operation operation) {
        return true;
    }

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
    default Object interceptReadSource(@Nullable Object source, PropertyMapping property, Operation operation) {
        return source;
    }

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
    default boolean supportInterceptSourceWrite(@Nullable Object sourceData, @Nullable Object target, PropertyMapping property, AssembleOperation operation) {
        return true;
    }

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
    default Object interceptSourceWrite(@Nullable Object sourceData, @Nullable Object target, PropertyMapping property, AssembleOperation operation) {
        return sourceData;
    }


}
