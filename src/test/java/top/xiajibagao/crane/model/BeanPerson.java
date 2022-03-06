package top.xiajibagao.crane.model;

import lombok.Data;
import lombok.experimental.Accessors;
import top.xiajibagao.crane.Gender;
import top.xiajibagao.crane.annotation.*;
import top.xiajibagao.crane.config.TestBeanContainer;
import top.xiajibagao.crane.container.EnumDictContainer;
import top.xiajibagao.crane.container.KeyValueContainer;
import top.xiajibagao.crane.impl.json.module.ProcessJson;

import java.util.List;

/**
 * @author huangchengxing
 * @date 2022/03/06 15:30
 */
@ProcessJson(targetClass = BeanPerson.class)
@Accessors(chain = true)
@Data
public class BeanPerson {

    @Sort(2)
    @Assemble(container = TestBeanContainer.class, propTemplates = JsonPerson.UserPropTemplate.class)
    private Integer id;
    private String name;
    private Integer age;

    @Sort(3)
    @Assemble(container = KeyValueContainer.class, namespace = "sex", props = @Prop(ref = "sexName"))
    private Integer sex;
    private String sexName;

    @Sort(1)
    @Assemble(container = EnumDictContainer.class, namespace = "sex", propTemplates = JsonPerson.UserPropTemplate.class,props = {
        @Prop(src = "id", ref = "genderId"),
        @Prop(src = "name", ref = "genderName")
    })
    private Gender gender;
    private Integer genderId;
    private String genderName;

    @PropsTemplate({
        @Prop(src = "id", ref = "id"),
        @Prop(src = "name", ref = "name"),
        @Prop(src = "age", ref = "age"),
        @Prop("user")
    })
    public interface UserPropTemplate{}

    @Disassemble(BeanPerson.class)
    List<BeanPerson> relatives;

}
