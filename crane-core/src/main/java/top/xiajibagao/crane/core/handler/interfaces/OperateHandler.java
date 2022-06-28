package top.xiajibagao.crane.core.handler.interfaces;

import top.xiajibagao.crane.core.helper.Orderly;
import top.xiajibagao.crane.core.operator.interfaces.*;

/**
 * <p>用于针对特定类型对象进行读写的操作处理器。一般会注册到{@link OperateProcessor}中使用。<br />
 * 其内部持有一个{@link OperateProcessor}实例，可通过{@link #getOperateProcessor()}获取。
 * 若当前处理器解析无法处理该类型的数据，则可能会将操作委托给该操作处理器，<br />
 * <b>若持有的处理器中注册了当前实例，则此行为有潜在导致{@link StackOverflowError}的可能性，需要实现类尤其注意。</b>
 *
 * @author huangchengxing
 * @date 2022/06/27 15:56
 */
public interface OperateHandler extends SourceReader, TargetWriter, Operator, OperateProcessorComponent, Orderly {

}
