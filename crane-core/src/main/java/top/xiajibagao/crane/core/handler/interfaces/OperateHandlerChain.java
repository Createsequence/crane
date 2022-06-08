package top.xiajibagao.crane.core.handler.interfaces;

import top.xiajibagao.crane.core.handler.AbstractOrderlyHandlerChain;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.Operation;
import top.xiajibagao.crane.core.parser.interfaces.PropertyMapping;

import java.util.List;

/**
 * 处理器链，用于组织多个操作处理器与数据源拦截器，从而完成针对某一些类型的数据读与写操作。
 *
 * <p>处理器链中同时拥有数据源拦截器链{@link #interceptors()}与操作处理器链{@link #handlers()}两条"链"。
 * 执行时，应当先执行拦截器链，再执行处理器链。
 *
 * <p>数据源拦截器链可以视为由多个拦截器组成，可按一定规则排序的纯粹责任链，约定：<br />
 * 当调用{@link #readFromSource(Object, PropertyMapping, Operation)}时，
 * 链上所有{@link SourceOperateInterceptor#supportInterceptReadSource(Object, PropertyMapping, Operation)}返回为ture的节点都应当被执行；<br />
 * 当调用{@link #writeToTarget(Object, Object, PropertyMapping, AssembleOperation)}时，
 * 链上所有{@link SourceOperateInterceptor#supportInterceptSourceWrite(Object, Object, PropertyMapping, AssembleOperation)}返回为ture的节点都应当被执行。
 *
 * <p>操作处理器可以视为由多个处理器组成，看按一定规则排序的不纯责任链，约定：<br />
 * 当调用{@link #readFromSource(Object, PropertyMapping, Operation)}时，
 * 排序最靠前，且{@link OperateHandler#sourceCanRead(Object, PropertyMapping, Operation)}返回为ture的节点才会被执行；
 * 当调用{@link #writeToTarget(Object, Object, PropertyMapping, AssembleOperation)}时，
 * 排序最靠前，且{@link OperateHandler#targetCanWrite(Object, Object, PropertyMapping, AssembleOperation)}返回为ture的节点才会被执行；
 *
 * @see OperateHandler
 * @see SourceOperateInterceptor
 * @see AbstractOrderlyHandlerChain
 * @author huangchengxing
 * @date 2022/04/08 20:35
 */
public interface OperateHandlerChain extends OperateHandler {

    /**
     * 获取操作拦截器链
     *
     * @return java.util.List<top.xiajibagao.crane.core.handler.interfaces.SourceOperateInterceptor>
     * @author huangchengxing
     * @date 2022/6/4 23:16
     * @since 0.5.5
     */
    List<SourceOperateInterceptor> interceptors();

    /**
     * 注册操作拦截器
     *
     * @param interceptor 拦截器
     * @return top.xiajibagao.crane.core.handler.interfaces.OperateHandlerChain
     * @author huangchengxing
     * @date 2022/6/4 23:18
     * @since 0.5.5
     */
    OperateHandlerChain addInterceptor(SourceOperateInterceptor interceptor);
    
    /**
     * 获取操作处理器链
     *
     * @return java.util.List<top.xiajibagao.crane.core.handlers.OperateHandler>
     * @author huangchengxing
     * @date 2022/4/8 20:35
     */
    List<OperateHandler> handlers();

    /**
     * 添加处理器节点
     *
     * @param handler 处理器节点
     * @return top.xiajibagao.crane.core.handlers.OperateHandlerChain
     * @author huangchengxing
     * @date 2022/4/8 21:08
     */
    OperateHandlerChain addHandler(OperateHandler handler);
    
    /**
     * 若{@link #sourceCanRead(Object, PropertyMapping, Operation)}方法返回ture，
     * 则返回{@link #readFromSource(Object, PropertyMapping, Operation)}的返回值，否则直接返回null
     *
     * @param source 数据源
     * @param property 待处理字段
     * @param operation 字段配置
     * @return java.lang.Object
     * @author huangchengxing
     * @date 2022/6/7 15:47
     * @since 0.5.5
     */
    default Object tryReadFromSource(Object source, PropertyMapping property, Operation operation) {
        return sourceCanRead(source, property, operation) ? readFromSource(source, property, operation) : null;
    }

    /**
     * 若{@link #targetCanWrite(Object, Object, PropertyMapping, AssembleOperation)}返回ture，
     * 则调用{@link #writeToTarget(Object, Object, PropertyMapping, AssembleOperation)}
     *
     * @param sourceData 从数据源获取的数据
     * @param target     待处理对象
     * @param property   待处理字段
     * @param operation  字段配置
     * @author huangchengxing
     * @date 2022/4/8 9:40
     * @since 0.5.5
     */
    default void tryWriteToTarget(Object sourceData, Object target, PropertyMapping property, AssembleOperation operation) {
        if (targetCanWrite(sourceData, target, property, operation)) {
            writeToTarget(sourceData, target, property, operation);
        }
    }

}
