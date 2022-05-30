package top.xiajiabagao.crane.starter.example.classanno;

import top.xiajibagao.crane.core.annotation.Assemble;
import top.xiajibagao.crane.core.annotation.Operations;
import top.xiajibagao.crane.core.annotation.Prop;
import top.xiajibagao.crane.core.annotation.PropsTemplate;
import top.xiajibagao.crane.core.container.EnumDictContainer;

/**
 * @author huangchengxing
 * @date 2022/05/24 11:22
 */
@Operations(assembles = @Assemble(key = "gender", container = EnumDictContainer.class, namespace = "gender", propTemplates = ClassB.GenderTemplate.class))
public class ClassB extends ClassA {
    @PropsTemplate({
        @Prop(src = "id", ref = "genderId"),
        @Prop(src = "name", ref = "genderName")
    })
    public interface GenderTemplate {}
}
