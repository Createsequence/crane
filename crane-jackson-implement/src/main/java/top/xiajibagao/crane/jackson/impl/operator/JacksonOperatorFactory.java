package top.xiajibagao.crane.jackson.impl.operator;

import com.fasterxml.jackson.databind.ObjectMapper;
import top.xiajibagao.crane.core.handler.AssembleHandlerChain;
import top.xiajibagao.crane.core.operator.interfaces.Assembler;
import top.xiajibagao.crane.core.operator.interfaces.Disassembler;
import top.xiajibagao.crane.core.operator.interfaces.OperatorFactory;

/**
 * @author huangchengxing
 * @date 2022/03/02 10:03
 */
public class JacksonOperatorFactory implements OperatorFactory {

    private final Assembler assembler;
    private final Disassembler disassembler;

    public JacksonOperatorFactory(ObjectMapper objectMapper, AssembleHandlerChain assembleHandlerChain) {
        this.assembler = new JacksonAssembler(objectMapper, assembleHandlerChain);
        this.disassembler = new JacksonDisassembler(objectMapper);
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
