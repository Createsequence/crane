package io.github.createsequence.crane.core.annotation;

import java.lang.annotation.*;

/**
 * 标识性注解，表示被注解的注解是元注解，用户可以基于元注解扩展自定义注解 <br />
 * 该注解中属性用于描述扩展注解的一些信息，与一些约定的规则。
 * 约定的规则不具备强制性，但是违背规则可能导致一些意外情况，用户需要自行处理。
 *
 * @author huangchengxing
 * @date 2022/03/03 16:12
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MateAnnotation {

    /**
     * 表明基于该元注解扩展的自定义注解是否允许重复
     */
    boolean repeatable() default false;

    /**
     * 表明基于该元注解扩展的自定义注解是否允许通过继承/实现关系传递
     */
    boolean transitive() default true;

    /**
     * 表明该元注解有哪些层级被实际使用
     */
    Class<? extends Annotation>[] used() default {};

    /**
     * 表示当前元注解的层级
     */
    int level() default 0;

    /**
     * 元注解的根节点
     */
    Class<? extends Annotation> root() default Annotation.class;

}
