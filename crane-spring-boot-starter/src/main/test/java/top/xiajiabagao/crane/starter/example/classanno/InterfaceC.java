package top.xiajiabagao.crane.starter.example.classanno;

import top.xiajibagao.crane.core.annotation.Disassemble;
import top.xiajibagao.crane.core.annotation.Operations;

/**
 * @author huangchengxing
 * @date 2022/05/24 11:22
 */
@Operations(disassembles = @Disassemble(key = "relatives", targetClass = Person.class))
public interface InterfaceC {
}
