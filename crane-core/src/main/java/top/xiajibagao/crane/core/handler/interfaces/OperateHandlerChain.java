package top.xiajibagao.crane.core.handler.interfaces;

import top.xiajibagao.crane.core.operator.interfaces.Assembler;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.Operation;
import top.xiajibagao.crane.core.parser.interfaces.PropertyMapping;

import java.util.List;

/**
 * 装配处理器链 <br />
 * 用于组织多个装配处理节点，根据一定的顺序筛选类型支持装的配处理器，
 * 并调用其用以在{@link Assembler}中用于从不同类型的数据源中根据配置获取所需要的数据，
 * 并将数据填充到不同类型的对象中。
 *
 * @author huangchengxing
 * @date 2022/04/08 20:35
 */
public interface OperateHandlerChain extends OperateHandler {

    /**
     * 获取处理器链
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
     * 处理器链中是否存在可以从数据源中读取数据的节点
     *
     * @param source 数据源
     * @param property 待处理字段
     * @param operation 字段配置
     * @return boolean
     * @author huangchengxing
     * @date 2022/4/8 21:04
     */
    @Override
    default boolean sourceCanRead(Object source, PropertyMapping property, Operation operation) {
        return handlers().stream().anyMatch(h -> h.sourceCanRead(source, property, operation));
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
     */
    @Override
    default boolean targetCanWrite(Object sourceData, Object target, PropertyMapping property, AssembleOperation operation) {
        return handlers().stream().anyMatch(h -> h.targetCanWrite(sourceData, target, property, operation));
    }
    
    /**
     * 从数据源中获取数据。将使用处理器链中第一个支持处理该类的节点进行处理。
     *
     * @param source 数据源
     * @param property 待处理字段
     * @param operation 字段配置
     * @return java.lang.Object
     * @author huangchengxing
     * @date 2022/4/8 21:05
     */
    @Override
    default Object readFromSource(Object source, PropertyMapping property, Operation operation) {
        return handlers()
            .stream()
            .filter(h -> h.sourceCanRead(source, property, operation))
            .findFirst()
            .map(h -> h.readFromSource(source, property, operation))
            .orElse(null);
    }

    /**
     * 将数据源中获取的数据写入待处理对象。将使用处理器链中第一个支持处理该类的节点进行处理。
     *
     * @param sourceData 从数据源获取的数据
     * @param target 待处理对象
     * @param property 待处理字段
     * @param operation 字段配置
     * @author huangchengxing
     * @date 2022/4/8 21:05
     */
    @Override
    default void writeToTarget(Object sourceData, Object target, PropertyMapping property, AssembleOperation operation) {
        handlers()
            .stream()
            .filter(h -> h.targetCanWrite(sourceData, target, property, operation))
            .findFirst()
            .ifPresent(h -> h.writeToTarget(sourceData, target, property, operation));
    }

}
