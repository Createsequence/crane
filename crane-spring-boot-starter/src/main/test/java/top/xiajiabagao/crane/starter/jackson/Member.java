package top.xiajiabagao.crane.starter.jackson;

import lombok.Data;
import lombok.experimental.Accessors;
import top.xiajibagao.annotation.Assemble;
import top.xiajibagao.annotation.ProcessJacksonNode;
import top.xiajibagao.annotation.Prop;
import top.xiajibagao.crane.core.container.KeyValueContainer;

/**
 * @author huangchengxing
 * @date 2022/04/10 22:47
 */
@ProcessJacksonNode
@Accessors(chain = true)
@Data
public class Member {

    private Integer classroomId;

    @Assemble(container = KeyValueContainer.class, namespace = "member", props = @Prop(src = "name", ref = "name"))
    private Integer id;
    private String name;

    @Assemble(container = KeyValueContainer.class, namespace = "sex", props = @Prop("sexName"))
    private Integer sex;
    private String sexName;

}
