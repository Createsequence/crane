package top.xiajibagao.crane.core.operator;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import lombok.Getter;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import top.xiajibagao.crane.core.annotation.GroupRegister;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandler;
import top.xiajibagao.crane.core.helper.Orderly;
import top.xiajibagao.crane.core.operator.interfaces.*;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.Operation;
import top.xiajibagao.crane.core.parser.interfaces.PropertyMapping;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * {@link OperateProcessor}的基础实现
 *
 * @author huangchengxing
 * @date 2022/06/27 14:32
 * @see 0.5.8
 */
@Getter
public abstract class AbstractOperateProcessor<T extends AbstractOperateProcessor<T>> implements OperateProcessor {

    private final List<TargetWriter> targetWriters = new ArrayList<>();
    private final List<TargetWriteInterceptor> targetWriteInterceptors = new ArrayList<>();
    private final List<SourceReader> sourceReaders = new ArrayList<>();
    private final List<SourceReadInterceptor> sourceReadInterceptors = new ArrayList<>();
    protected final String[] registerGroups;
    @SuppressWarnings("unchecked")
    private final T typedThis = (T) this;

    protected AbstractOperateProcessor(@Nonnull String... defaultRegisterGroups) {
        this.registerGroups = Optional.ofNullable(this.getClass())
            .map(t -> AnnotatedElementUtils.findMergedAnnotation(t, GroupRegister.class))
            .map(GroupRegister::value)
            .orElse(defaultRegisterGroups);
        Assert.notNull(this.registerGroups, "registerGroups must not null");
    }

    // ============================ register ============================

    /**
     * 注册待处理对象写入器
     *
     * @param targetWriters 待处理对象写入器
     * @return T
     * @author huangchengxing
     * @date 2022/6/27 14:43
     * @see #register(List, Orderly[])
     */
    @Override
    public T registerTargetWriters(TargetWriter... targetWriters) {
        return register(this.targetWriters, targetWriters);
    }
    
    /**
     * 注册待处理对象写入拦截器
     *
     * @param targetWriteInterceptors 待处理对象写入拦截器
     * @return T
     * @author huangchengxing
     * @date 2022/6/27 14:44
     * @see #register(List, Orderly[])
     */
    @Override
    public T registerTargetWriteInterceptors(TargetWriteInterceptor... targetWriteInterceptors) {
        return register(this.targetWriteInterceptors, targetWriteInterceptors);
    }
    
    /**
     * 注册数据源读取器
     *
     * @param sourceReaders 数据源读取器
     * @return T
     * @author huangchengxing
     * @date 2022/6/27 14:45
     * @see #register(List, Orderly[])
     */
    @Override
    public T registerSourceReaders(SourceReader... sourceReaders) {
        return register(this.sourceReaders, sourceReaders);
    }
    
    /**
     * 注册数据源读取拦截器
     *
     * @param sourceReadInterceptors 数据源读取拦截器
     * @return T
     * @author huangchengxing
     * @date 2022/6/27 14:45
     * @see #register(List, Orderly[])
     */
    @Override
    public T registerSourceReadInterceptors(SourceReadInterceptor... sourceReadInterceptors) {
        return register(this.sourceReadInterceptors, sourceReadInterceptors);
    }

    /**
     * 注册{@link OperateHandler}
     *
     * @param  operateHandler operateHandler
     * @return T
     * @author huangchengxing
     * @date 2022/6/27 17:33
     * @see #register(List, Orderly[])
     */
    public T registerHandlers(OperateHandler... operateHandler) {
        registerTargetWriters(operateHandler);
        registerSourceReaders(operateHandler);
        return typedThis;
    }

    /**
     * 将以下符合条件的组件注册到指定列表：
     * <ul>
     *     <li>组件不为null；</li>
     *     <li>组件在列表中不存在；</li>
     *     <li>组件不为{@link OperateProcessor}；</li>
     *     <li>组件的注册组必须与当前有处理器所属的组成组有交集；</li>
     *     <li>组件在列表中不存在；</li>
     * </ul>
     */
    @SafeVarargs
    protected final <I extends Orderly & GroupRegistrable> T register(List<I> list, I... items) {
        if (ArrayUtil.isEmpty(items)) {
            return typedThis;
        }
        Stream.of(items)
            .filter(Objects::nonNull)
            .filter(t -> !list.contains(t))
            .filter(t -> !ClassUtil.isAssignable(OperateProcessor.class, AopProxyUtils.ultimateTargetClass(t)))
            .filter(this::isRegistrable)
            .forEach(list::add);
        list.sort(Orderly.comparator());
        return typedThis;
    }


    // ============================ execute ============================

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
        return getSourceReaders().stream().anyMatch(h -> h.sourceCanRead(source, property, operation));
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
    @Nullable
    public Object readFromSource(Object source, PropertyMapping property, Operation operation) {
        for (SourceReadInterceptor interceptor : getSourceReadInterceptors()) {
            if (interceptor.supportInterceptReadSource(source, property, operation)) {
                source = interceptor.interceptReadSource(source, property, operation);
            }
        }
        final Object interceptedSources = source;
        return getSourceReaders()
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
    @Nullable
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
        return getTargetWriters().stream().anyMatch(h -> h.targetCanWrite(sourceData, target, property, operation));
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
        for (TargetWriteInterceptor interceptor : getTargetWriteInterceptors()) {
            if (interceptor.supportInterceptSourceWrite(sourceData, target, property, operation)) {
                sourceData = interceptor.interceptSourceWrite(sourceData, target, property, operation);
            }
        }
        final Object interceptedSourcesData = sourceData;
        getTargetWriters()
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
