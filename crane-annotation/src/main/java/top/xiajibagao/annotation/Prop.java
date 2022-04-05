package top.xiajibagao.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author huangchengxing
 * @date 2022/02/28 18:01
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Prop {

    /**
     * 要处理的目标对象中的字段
     */
    @AliasFor("ref")
    String value() default "";

    /**
     * 要处理的目标对象中的字段
     */
    @AliasFor("value")
    String ref() default "";

    // TODO SpEL表达式支持
    /**
     * 当数据源为对象类型时，可以选择性的引用其字段，当为空时默认引用整个数据源对象。
     * 若数据源不为对象类型时，该字段配置无效。
     */
    String src() default "";

}
