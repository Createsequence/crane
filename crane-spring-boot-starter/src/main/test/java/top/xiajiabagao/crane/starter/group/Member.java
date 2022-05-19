package top.xiajiabagao.crane.starter.group;

import lombok.Data;
import lombok.experimental.Accessors;
import top.xiajiabagao.crane.starter.common.TestContainer;
import top.xiajibagao.crane.core.annotation.Assemble;
import top.xiajibagao.crane.core.annotation.Disassemble;
import top.xiajibagao.crane.core.annotation.Prop;

import java.util.List;

/**
 * @author huangchengxing
 * @date 2022/05/19 8:49
 */
@Data
@Accessors(chain = true)
public class Member {

    @Assemble(container = TestContainer.class, groups = UserGroup.class, props = {
        @Prop(src = "beanName", ref = "name"),
        @Prop(src = "beanAge", ref = "age"),
    })
    private Integer id;
    private String name;
    private Integer age;

    @Assemble(namespace = "sex", props = @Prop("sexName"))
    private Integer sex;
    private String sexName;

    @Disassemble(Member.class)
    List<Member> relatives;

}
