package top.xiajibagao.crane.core.operator.interfaces;

/**
 * 操作者工厂，用于生产{@link Assembler}与{@link Disassembler}实例
 *
 * @author huangchengxing
 * @date 2022/03/01 15:20
 */
public interface OperatorFactory {

    /**
     * 获取装配器
     *
     * @return top.xiajibagao.crane.operator.interfaces.Assembler
     * @author huangchengxing
     * @date 2022/3/1 15:22
     */
    Assembler getAssembler();

    /**
     * 获取拆卸器
     *
     * @return top.xiajibagao.crane.operator.interfaces.Disassembler
     * @author huangchengxing
     * @date 2022/3/1 15:22
     */
    Disassembler getDisassembler();

}
