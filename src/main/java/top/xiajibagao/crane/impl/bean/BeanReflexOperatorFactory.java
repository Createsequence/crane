package top.xiajibagao.crane.impl.bean;

import top.xiajibagao.crane.operator.interfaces.Assembler;
import top.xiajibagao.crane.operator.interfaces.Disassembler;
import top.xiajibagao.crane.operator.interfaces.OperatorFactory;

/**
 * @author huangchengxing
 * @date 2022/03/02 13:29
 */
public class BeanReflexOperatorFactory implements OperatorFactory {

    private final Assembler assembler;
    private final Disassembler disassembler;

    public BeanReflexOperatorFactory() {
        this.assembler = new BeanReflexAssembler();
        this.disassembler =  new BeanReflexDisassembler();
    }

    @Override
    public Assembler getAssembler() {
        return assembler;
    }

    @Override
    public Disassembler getDisassembler() {
        return disassembler;
    }

}
