package top.xiajiabagao.crane.starter.example.classanno;

import top.xiajibagao.crane.core.annotation.Assemble;
import top.xiajibagao.crane.core.annotation.Operations;
import top.xiajibagao.crane.core.annotation.Prop;

/**
 * @author huangchengxing
 * @date 2022/05/24 11:22
 */
@Operations(assembles = @Assemble(key = "sex", namespace = "sex", props = @Prop("sexName")))
public class ClassA {
}
