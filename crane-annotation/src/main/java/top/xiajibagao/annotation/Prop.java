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

    /**
     * 当数据源为对象类型时，可以选择性的引用其字段，当为空时默认引用整个数据源对象。
     * 若数据源不为对象类型时，该字段配置无效。
     */
    String src() default "";

    /**
     * 当该参数不为空时，将在处理字段时执行该表达式，并将表达式返回值作为新的数据源 <br />
     * 表达式默认已经注册参数：
     * <ul>
     *     <li>#source: 数据源对象；</li>
     *     <li>#target: 待处理对象；</li>
     *     <li>#key: key字段的值；</li>
     *     <li>#src: {@link #src()}指定的参数值；</li>
     *     <li>#ref: {@link #ref()}指定的参数值；</li>
     * </ul>
     */
    String exp() default "";

    /**
     * SpEL表达式返回值类型
     */
    Class<?> expType() default Void.class;

}
