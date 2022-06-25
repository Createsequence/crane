package top.xiajibagao.crane.core.parser.interfaces;

import java.util.Collection;

/**
 * 动态字段装卸注解
 *
 * <p>功能与{@link DynamicDisassembleOperation}一致，但是代表的类型不确定的装卸字段，
 * 用于处理类似字段类型是泛型、接口或父类的情况。
 *
 * <p>使用时，应当通过{@link DisassembleOperation#isDynamic(DisassembleOperation)}确定要处理的
 * {@link DisassembleOperation}是否属于{@link DynamicDisassembleOperation}，若是，则应根据当前
 * 待处理的对象，调用{@link #resolve(Object)}获得确定类型的{@link DisassembleOperation}。<br />
 * {@link DynamicDisassembleOperation#getTargetOperateConfiguration()}不允许被直接调用。
 *
 * @author huangchengxing
 * @date 2022/06/24 11:21
 * @since 0.5.7
 * @see DisassembleOperation
 */
public interface DynamicDisassembleOperation extends DisassembleOperation {

    /**
     * 默认调用时应直接抛出异常，实现类需要调用{@link #resolve(Object)}将其转为正常的{@link DisassembleOperation}使用
     *
     * @return top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration
     * @author huangchengxing
     * @date 2022/6/24 13:55
     * @throws UnsupportedOperationException 调用时抛出
     */
    @Override
    default OperationConfiguration getTargetOperateConfiguration() {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取实际对象类型对应的操作配置
     *
     * <p>若该对象类型为可能嵌套的数组或{@link Collection}集合，
     * 则将递归遍历，直到获取到第一个非数组或{@link Collection}集合的元素，
     * 作为实际待解析的对象。<br />
     * 当对象为空时，或无法确定实际类型时，将返回null。
     *
     * @param target 待处理器对象
     * @return top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration
     * @author huangchengxing
     * @date 2022/6/24 17:06
     */
    OperationConfiguration getTargetOperateConfiguration(Object target);

    /**
     * 获取当确定类型的装卸操作，{@link DisassembleOperation#getTargetOperateConfiguration()}获取的实例
     * 应当与{@link #getTargetOperateConfiguration(Object)}相同
     *
     * @param target 待拆卸的字段值
     * @return top.xiajibagao.crane.core.parser.interfaces.DisassembleOperation
     * @author huangchengxing
     * @date 2022/6/24 16:30
     */
    DisassembleOperation resolve(Object target);

}
