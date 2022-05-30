package top.xiajiabagao.crane.starter.example.core;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import top.xiajiabagao.crane.starter.example.common.Gender;
import top.xiajiabagao.crane.starter.example.common.TestContainer;
import top.xiajibagao.crane.core.annotation.Assemble;
import top.xiajibagao.crane.core.annotation.Disassemble;
import top.xiajibagao.crane.core.annotation.Prop;
import top.xiajibagao.crane.core.annotation.PropsTemplate;
import top.xiajibagao.crane.core.container.EnumDictContainer;
import top.xiajibagao.crane.core.container.KeyValueContainer;

import java.util.List;

/**
 * @author huangchengxing
 * @date 2022/04/09 20:23
 */
@EqualsAndHashCode(exclude = "relatives")
@Accessors(chain = true)
@Data
public class Person {

    @Assemble(container = TestContainer.class, props = {
        @Prop(src = "beanName", ref = "name"),
        @Prop(src = "beanAge", ref = "age"),
    })
    private Integer id;
    private String name;
    private Integer age;

    @Assemble(container = KeyValueContainer.class, namespace = "sex", props = @Prop("sexName"))
    private Integer sex;
    private String sexName;

    @Assemble(container = EnumDictContainer.class, namespace = "gender", propTemplates = GenderTemplate.class)
    private Gender gender;
    private Integer genderId;
    private String genderName;

    @Disassemble(Person.class)
    List<Person> relatives;

    @PropsTemplate({
        @Prop(src = "id", ref = "genderId"),
        @Prop(src = "name", ref = "genderName")
    })
    public interface GenderTemplate {}

}
