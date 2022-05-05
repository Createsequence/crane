package top.xiajiabagao.crane.starter.extension;

import lombok.Data;
import lombok.experimental.Accessors;
import top.xiajiabagao.crane.starter.common.TestContainer;
import top.xiajibagao.crane.core.annotation.Assemble;
import top.xiajibagao.crane.core.annotation.Prop;
import top.xiajibagao.crane.core.container.KeyValueContainer;

/**
 * @author huangchengxing
 * @date 2022/04/10 22:47
 */
@Accessors(chain = true)
@Data
public class Member {

    private Integer classroomId;

    @Assemble(container = TestContainer.class, props = {
        @Prop(src = "beanName", ref = "name"),
        @Prop(src = "beanAge", ref = "age"),
    })
    private Integer id;
    private String name;

    @Assemble(container = KeyValueContainer.class, namespace = "sex", props = @Prop("sexName"))
    private Integer sex;
    private String sexName;

}
