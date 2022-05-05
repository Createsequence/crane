package top.xiajibagao.crane.core.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 数据装卸注解
 * <p>注解在嵌套字段上，将会在处理数据时将注解字段递归并展开为复数需要进行装配操作的对象
 *
 * @author huangchengxing
 * @date 2022/02/28 18:00
 */
@MateAnnotation
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Disassemble {

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
     * 字段别名。
     * 仅当无法根据注解字段名找到key字段时，才尝试通过别名找到至少一个存在的字段。
     */
    String[] aliases() default {};

}
