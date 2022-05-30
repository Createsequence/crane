package top.xiajiabagao.crane.starter.example.jackson;

import lombok.Data;
import lombok.experimental.Accessors;
import top.xiajibagao.crane.core.annotation.Assemble;
import top.xiajibagao.crane.core.annotation.Disassemble;
import top.xiajibagao.crane.core.annotation.Prop;
import top.xiajibagao.crane.jackson.impl.annotation.ProcessJacksonNode;
import top.xiajibagao.crane.jackson.impl.operator.JacksonAssembler;
import top.xiajibagao.crane.jackson.impl.operator.JacksonDisassembler;

import java.util.List;

/**
 * @author huangchengxing
 * @date 2022/04/09 20:23
 */
@ProcessJacksonNode
@Accessors(chain = true)
@Data
public class Classroom {

    @Assemble(assembler = JacksonAssembler.class, namespace = "student", props = @Prop(src = "name", ref = "studentNames"))
    @Assemble(assembler = JacksonAssembler.class, namespace = "student", props = @Prop("students"))
    private Integer id;
    private String name;

    List<Member> students;
    List<String> studentNames;

    @Disassemble(disassembler = JacksonDisassembler.class, targetClass = Member.class)
    List<Member> members;

}
