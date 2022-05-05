package top.xiajibagao.crane.core.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 解析配置，用于在可能需要的地方声明配置信息
 *
 * @author huangchengxing
 * @date 2022/03/06 16:50
 */
@MateAnnotation
@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigOption {

    /**
     * 待处理的目标类型
     */
    @AliasFor("targetClass")
    Class<?> value() default Void.class;

    /**
     * 待处理的目标类型
     */
    @AliasFor("value")
    Class<?> targetClass() default Void.class;

    /**
     * 要使用的配置解析器
     */
    Class<?> parser() default Void.class;

    /**
     * 要使用的操作者工厂
     */
    Class<?> operatorFactory() default Void.class;

    /**
     * 要使用的执行器
     */
    Class<?> executor() default Void.class;

}
