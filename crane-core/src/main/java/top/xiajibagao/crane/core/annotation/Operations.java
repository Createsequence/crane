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
     * 扩展注解，效果类似继承，将从指定类型上获取{@link Operations}注解
     */
    Class<?>[] extendFrom() default {};

    /**
     * 不扩展的{@link #extendFrom()}指定的类或该类扩展树上的类
     */
    Class<?>[] extendExcludes() default {};

}
