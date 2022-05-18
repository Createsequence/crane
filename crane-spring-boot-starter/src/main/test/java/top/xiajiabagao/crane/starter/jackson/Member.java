package top.xiajiabagao.crane.starter.jackson;

import lombok.Data;
import lombok.experimental.Accessors;
import top.xiajibagao.crane.core.annotation.Assemble;
import top.xiajibagao.crane.core.annotation.Prop;
import top.xiajibagao.crane.jackson.impl.annotation.ProcessJacksonNode;
import top.xiajibagao.crane.jackson.impl.operator.JacksonAssembler;

/**
 * @author huangchengxing
 * @date 2022/04/10 22:47
 */
@ProcessJacksonNode
@Accessors(chain = true)
@Data
public class Member {

    private Integer classroomId;

    @Assemble(assembler = JacksonAssembler.class, namespace = "member", props = @Prop(src = "name", ref = "name"))
    private Integer id;
    private String name;

    @Assemble(assembler = JacksonAssembler.class, namespace = "sex", props = @Prop("sexName"))
    private Integer sex;
    private String sexName;

}
