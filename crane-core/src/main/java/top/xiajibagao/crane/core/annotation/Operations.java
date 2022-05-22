package top.xiajibagao.crane.core.annotation;

import java.lang.annotation.*;

/**
 * 批量操作注解，标记在类上以声明当前类所需要的一组装配操作
 *
 * @author huangchengxing 
 * @date 2022/5/21 13:33
 */
@MateAnnotation
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Operations {

    /**
     * 装配操作
     */
    Assemble[] assembles() default {};

    /**
     * 装卸操作
     */
    Disassemble[] disassembles() default {};

    /**
     * 不继承指定类及其父类或接口上的注解
     */
    Class<?>[] extendExcludes() default {};

    /**
     * 继承当前类父类及实现接口上的注解
     */
    boolean enableExtend() default true;

}
