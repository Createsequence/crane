package top.xiajiabagao.crane.starter.classanno;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import top.xiajiabagao.crane.starter.common.Gender;
import top.xiajiabagao.crane.starter.common.TestContainer;
import top.xiajibagao.crane.core.annotation.Assemble;
import top.xiajibagao.crane.core.annotation.Operations;
import top.xiajibagao.crane.core.annotation.Prop;

import java.util.List;

/**
 * @author huangchengxing
 * @date 2022/04/09 20:23
 */
@EqualsAndHashCode(callSuper = true)
@Operations(
    assembles = @Assemble(key = "uid", aliases = "id", container = TestContainer.class, props = {
        @Prop(src = "beanName", ref = "name"),
        @Prop(src = "beanAge", ref = "age"),
    }),
    extendExcludes = ClassA.class
)
@Accessors(chain = true)
@Data
public class Person extends ClassB implements InterfaceC {

    private Integer id;
    private String name;
    private Integer age;

    private Integer sex;
    private String sexName;

    private Gender gender;
    private Integer genderId;
    private String genderName;

    List<Person> relatives;

}
