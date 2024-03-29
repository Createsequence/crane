package io.github.createsequence.crane.core.operator.interfaces;

import io.github.createsequence.crane.core.helper.Orderly;
import io.github.createsequence.crane.core.parser.interfaces.AssembleOperation;
import io.github.createsequence.crane.core.parser.interfaces.Operation;
import io.github.createsequence.crane.core.parser.interfaces.PropertyMapping;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * 操作处理器，用于操作指定类型的对象实例，通过从中获取或写入数据
 *
 * <p>操作者分别具备数据源读取器链{@link #getSourceReaders()}与待处理对象编写器链{@link #getTargetWriters()},
 * 前者用于根据配置从指定数据源中获取数据，后者则用于根据配置将数据源写入待处理对象。
 * 两条链都应该被视为可按照{@link Orderly}的定义顺序排序的存粹责任链，
 * 即在处理某类特定的数据源或待处理对象时，两者都按顺序筛选出唯一匹配的节点（若存在的话）用于处理输入数据。
 *
 * <p>操作者还具备读取拦截器链{@link #getSourceReadInterceptors()}和写入拦截器链{@link #getTargetWriteInterceptors()}，
 * 两者分别与数据源读取器链{@link #getSourceReaders()}与待处理对象编写器链{@link #getTargetWriters()}对应。
 * 拦截器应当被视为一个可按照{@link Orderly}的定义顺序排序不纯责任链，
 * 在其对应的Reader与Writer之前被调用之前，当前操作者中任何符合的拦截器都应该被依次调用。
 *
 * <p>操作者允许注册{@link TargetWriter}, {@link SourceReader},{@link TargetWriteInterceptor}
 * 与{@link SourceReadInterceptor}。其中，允许注册的组件都应当符合下述规则：
 * <ul>
 *     <li>组件不能也是一个{@link OperateProcessor}；</li>
 *     <li>组件必须使当前实例的{@link #isRegistrable(OperateProcessorComponent)}方法返回{@code true}；</li>
 * </ul>
 *
 * @author huangchengxing
 * @date 2022/06/27 15:17
 * @since 0.6.0
 */
public interface OperateProcessor extends TargetWriter, SourceReader, OperateProcessorComponent, Orderly {

    /**
     * 注册待处理对象写入器
     *
     * @param targetWriters 待处理对象写入器
     * @return interfaces.operator.io.github.createsequence.crane.core.OperateProcessor
     * @author huangchengxing
     * @date 2022/6/27 14:43
     */
    OperateProcessor registerTargetWriters(TargetWriter... targetWriters);

    /**
     * 注册待处理对象写入拦截器
     *
     * @param targetWriteInterceptors 待处理对象写入拦截器
     * @return interfaces.operator.io.github.createsequence.crane.core.OperateProcessor
     * @author huangchengxing
     * @date 2022/6/27 14:44
     */
    OperateProcessor registerTargetWriteInterceptors(TargetWriteInterceptor... targetWriteInterceptors);

    /**
     * 注册数据源读取器
     *
     * @param sourceReaders 数据源读取器
     * @return interfaces.operator.io.github.createsequence.crane.core.OperateProcessor
     * @author huangchengxing
     * @date 2022/6/27 14:45
     */
    OperateProcessor registerSourceReaders(SourceReader... sourceReaders);

    /**
     * 注册数据源读取拦截器
     *
     * @param sourceReadInterceptors 数据源读取拦截器
     * @return interfaces.operator.io.github.createsequence.crane.core.OperateProcessor
     * @author huangchengxing
     * @date 2022/6/27 14:45
     */
    OperateProcessor registerSourceReadInterceptors(SourceReadInterceptor... sourceReadInterceptors);

    /**
     * 获取待处理对象编写器
     *
     * @return java.util.List<interfaces.operator.io.github.createsequence.crane.core.TargetWriter>
     * @author huangchengxing
     * @date 2022/6/27 14:18
     */
    default List<TargetWriter> getTargetWriters() {
        return Collections.emptyList();
    }

    /**
     * 获取待处理对象写入拦截器
     *
     * @return java.util.List<interfaces.operator.io.github.createsequence.crane.core.TargetWriteInterceptor>
     * @author huangchengxing
     * @date 2022/6/27 14:19
     */
    default List<TargetWriteInterceptor> getTargetWriteInterceptors() {
        return Collections.emptyList();
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
     */
    void tryWriteToTarget(Object sourceData, Object target, PropertyMapping property, AssembleOperation operation);

    /**
     * 获取数据源读取器
     *
     * @return java.util.List<interfaces.operator.io.github.createsequence.crane.core.SourceReader>
     * @author huangchengxing
     * @date 2022/6/27 14:21
     */
    default List<SourceReader> getSourceReaders() {
        return Collections.emptyList();
    }

    /**
     * 获取数据源读取拦截器
     *
     * @return java.util.List<interfaces.operator.io.github.createsequence.crane.core.SourceReadInterceptor>
     * @author huangchengxing
     * @date 2022/6/27 14:21
     */
    default List<SourceReadInterceptor> getSourceReadInterceptors() {
        return Collections.emptyList();
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
     */
    @Nullable
    Object tryReadFromSource(Object source, PropertyMapping property, Operation operation);

}
