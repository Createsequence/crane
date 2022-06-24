package top.xiajibagao.crane.core.parser.interfaces;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * 动态字段装卸注解
 * <p>功能与{@link DynamicDisassembleOperation}一致，但是代表的类型不确定的装卸字段，
 * 用于处理类似字段类型是泛型、接口或父类的情况。
 *
 * @author huangchengxing
 * @date 2022/06/24 11:21
 * @see DisassembleOperation
 * @since 0.5.7
 */
public interface DynamicDisassembleOperation extends DisassembleOperation {

    /**
     * 获取对象的实际类型的操作配置
     *
     * <p>若该对象类型为可能嵌套的数组或{@link Collection}集合，
     * 则将递归遍历，直到获取到第一个非数组或{@link Collection}集合的元素，
     * 作为实际待解析的对象。<br />
     * 当对象为空时，或无法确定实际类型时，将返回一个null。
     *
     * @return java.lang.Class<?>
     * @author huangchengxing
     * @date 2022/6/24 11:30
     */
    @Nullable
    OperationConfiguration getTargetOperateConfiguration(Object target);

    /**
     * 默认调用时应直接抛出异常，实现类需要调用{@link #getTargetOperateConfiguration(Object)}以实现相同的效果
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

}
