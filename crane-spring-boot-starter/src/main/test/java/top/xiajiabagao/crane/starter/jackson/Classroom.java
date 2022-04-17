package top.xiajiabagao.crane.starter.jackson;

import lombok.Data;
import lombok.experimental.Accessors;
import top.xiajibagao.annotation.Assemble;
import top.xiajibagao.annotation.Disassemble;
import top.xiajibagao.annotation.ProcessJacksonNode;
import top.xiajibagao.annotation.Prop;
import top.xiajibagao.crane.core.container.KeyValueContainer;

import java.util.List;

/**
 * @author huangchengxing
 * @date 2022/04/09 20:23
 */
@ProcessJacksonNode
@Accessors(chain = true)
@Data
public class Classroom {

    @Assemble(container = KeyValueContainer.class, namespace = "student", props = @Prop(src = "name", ref = "studentNames"))
    @Assemble(container = KeyValueContainer.class, namespace = "student", props = @Prop("students"))
    private Integer id;
    private String name;

    List<Member> students;
    List<String> studentNames;

    @Disassemble(Member.class)
    List<Member> members;

}
