package top.xiajibagao.crane.core.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 字段装配配置
 * <p>表示一次装配操作中，数据源对象的字段与待处理对象的字段的映射关系
 *
 * @author huangchengxing
 * @date 2022/02/28 18:01
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Prop {

    /**
     * 引用字段，需要将数据源映射到待处理对象的字段
     */
    @AliasFor("ref")
    String value() default "";

    /**
     * 引用字段，待处理对象中需要被数据源映射的字段
     */
    @AliasFor("value")
    String ref() default "";

    /**
     * 数据源字段，数据源对象中需要映射到待处理对象的字段
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
     * 该功能需要由装配器提供支持
     */
    String exp() default "";

    /**
     * SpEL表达式返回值类型
     */
    Class<?> expType() default Void.class;

}
