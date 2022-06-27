package top.xiajibagao.crane.core.operator.interfaces;

/**
 * 操作者，表明一个具有{@link OperateProcessor}，能够对指定类型的对象实例进行读取或者写入操作的实例
 *
 * @author huangchengxing
 * @date 2022/03/01 15:54
 * @see Assembler
 * @see Disassembler
 */
public interface Operator {

    /**
     * 获取操作处理器
     *
     * @return top.xiajibagao.crane.core.operator.interfaces.OperateProcessor
     * @author huangchengxing
     * @date 2022/6/27 15:25
     * @since 0.5.8
     */
    OperateProcessor getOperateProcessor();

}
