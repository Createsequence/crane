package top.xiajibagao.crane.core.handler;

import lombok.Getter;
import lombok.experimental.Accessors;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandler;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandlerChain;
import top.xiajibagao.crane.core.handler.interfaces.SourceOperateInterceptor;
import top.xiajibagao.crane.core.helper.Orderly;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.Operation;
import top.xiajibagao.crane.core.parser.interfaces.PropertyMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link #handlers()}与{@link #interceptors()}皆按{@link Orderly#comparator()}排序的处理器链，提供一些方法的基本实现
 *
 * @since 0.2.0
 * @author huangchengxing
 * @date 2022/04/08 21:02
 */
@Getter
@Accessors(fluent = true)
public class AbstractOrderlyHandlerChain implements OperateHandlerChain {

    protected final List<OperateHandler> handlers = new ArrayList<>();
    protected final List<SourceOperateInterceptor> interceptors = new ArrayList<>();

    @Override
    public OperateHandlerChain addInterceptor(SourceOperateInterceptor interceptor) {
        interceptors.add(interceptor);
        interceptors.sort(SourceOperateInterceptor::compareTo);
        return this;
    }

    @Override
    public OperateHandlerChain addHandler(OperateHandler handler) {
        handlers.add(handler);
        handlers.sort(OperateHandler::compareTo);
        return this;
    }

    /**
     * 处理器链中是否存在可以从数据源中读取数据的节点
     *
     * @param source 数据源
     * @param property 待处理字段
     * @param operation 字段配置
     * @return boolean
     * @author huangchengxing
     * @date 2022/4/8 21:04
     * @since 0.5.6
     */
    @Override
    public boolean sourceCanRead(Object source, PropertyMapping property, Operation operation) {
        return handlers().stream().anyMatch(h -> h.sourceCanRead(source, property, operation));
    }

    /**
     * 将数据源对象使用操作拦截器处理后，再使用处理器链中第一个支持处理该拦截诡异的数据源对象的操作处理器，从中获取所需数据源
     *
     * @param source 数据源
     * @param property 待处理字段
     * @param operation 字段配置
     * @return java.lang.Object
     * @author huangchengxing
     * @date 2022/4/8 21:05
     * @since 0.5.6
     */
    @Override
    public Object readFromSource(Object source, PropertyMapping property, Operation operation) {
        for (SourceOperateInterceptor interceptor : interceptors()) {
            if (interceptor.supportInterceptReadSource(source, property, operation)) {
                source = interceptor.interceptReadSource(source, property, operation);
            }
        }
        final Object interceptedSources = source;
        return handlers()
            .stream()
            .filter(h -> h.sourceCanRead(interceptedSources, property, operation))
            .findFirst()
            .map(h -> h.readFromSource(interceptedSources, property, operation))
            .orElse(null);
    }

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
     * @since 0.5.6
     */
    @Override
    public Object tryReadFromSource(Object source, PropertyMapping property, Operation operation) {
        return sourceCanRead(source, property, operation) ? readFromSource(source, property, operation) : null;
    }

    /**
     * 处理器链中是否存在可以将数据源数据写入待处理对象的节点
     *
     * @param sourceData 从数据源获取的数据
     * @param target 待处理对象
     * @param property 待处理字段
     * @param operation 字段配置
     * @return boolean
     * @author huangchengxing
     * @date 2022/4/8 9:40
     * @since 0.5.6
     */
    @Override
    public boolean targetCanWrite(Object sourceData, Object target, PropertyMapping property, AssembleOperation operation) {
        return handlers().stream().anyMatch(h -> h.targetCanWrite(sourceData, target, property, operation));
    }

    /**
     * 将数据源数据使用操作拦截器处理后，再使用处理器链中第一个支持处理该待处理对象的操作处理器，将拦截后的数据源数据写入待处理对象
     *
     * @param sourceData 从数据源获取的数据
     * @param target 待处理对象
     * @param property 待处理字段
     * @param operation 字段配置
     * @author huangchengxing
     * @date 2022/4/8 21:05
     * @since 0.5.6
     */
    @Override
    public void writeToTarget(Object sourceData, Object target, PropertyMapping property, AssembleOperation operation) {
        for (SourceOperateInterceptor interceptor : interceptors()) {
            if (interceptor.supportInterceptSourceWrite(sourceData, target, property, operation)) {
                sourceData = interceptor.interceptSourceWrite(sourceData, target, property, operation);
            }
        }
        final Object interceptedSourcesData = sourceData;
        handlers()
            .stream()
            .filter(h -> h.targetCanWrite(interceptedSourcesData, target, property, operation))
            .findFirst()
            .ifPresent(h -> h.writeToTarget(interceptedSourcesData, target, property, operation));
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
     * @since 0.5.6
     */
    @Override
    public void tryWriteToTarget(Object sourceData, Object target, PropertyMapping property, AssembleOperation operation) {
        if (targetCanWrite(sourceData, target, property, operation)) {
            writeToTarget(sourceData, target, property, operation);
        }
    }

}
