package top.xiajibagao.crane.impl.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import top.xiajibagao.crane.operator.interfaces.Assembler;
import top.xiajibagao.crane.operator.interfaces.Disassembler;
import top.xiajibagao.crane.operator.interfaces.OperatorFactory;

/**
 * @author huangchengxing
 * @date 2022/03/02 10:03
 */
@RequiredArgsConstructor
public class JacksonOperatorFactory implements OperatorFactory {

    private final Assembler assembler;
    private final Disassembler disassembler;

    public JacksonOperatorFactory(ObjectMapper objectMapper) {
        this.assembler = new JacksonAssembler(objectMapper);
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
