package top.xiajibagao.crane.model;

import lombok.Data;
import lombok.experimental.Accessors;
import top.xiajibagao.crane.Gender;
import top.xiajibagao.crane.annotation.*;
import top.xiajibagao.crane.config.TestBeanContainer;
import top.xiajibagao.crane.container.EnumDictContainer;
import top.xiajibagao.crane.container.KeyValueContainer;

import java.util.List;

/**
 * @author huangchengxing
 * @date 2021/12/28 14:34
 */
@Accessors(chain = true)
@Data
public class JsonPerson {

    @Sort(2)
    @Assemble(container = TestBeanContainer.class, propTemplates = UserPropTemplate.class)
    private Integer id;
    private String name;
    private Integer age;

    @Sort(3)
    @Assemble(container = KeyValueContainer.class, namespace = "sex")
    private Integer sex;

    @Sort(1)
    @Assemble(container = EnumDictContainer.class, namespace = "sex", propTemplates = UserPropTemplate.class,props = {
        @Prop(src = "id", ref = "gender"),
        @Prop(src = "name", ref = "genderName")
    })
    private Gender gender;

    @PropsTemplate({
        @Prop(src = "id", ref = "id"),
        @Prop(src = "name", ref = "name"),
        @Prop(src = "age", ref = "age"),
        @Prop("user")
    })
    public interface UserPropTemplate{}

    @Disassemble(JsonPerson.class)
    List<JsonPerson> relatives;

}
