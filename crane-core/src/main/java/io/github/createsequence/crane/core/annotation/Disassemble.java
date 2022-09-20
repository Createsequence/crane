package io.github.createsequence.crane.core.annotation;

import io.github.createsequence.crane.core.operator.BeanReflexDisassembler;
import io.github.createsequence.crane.core.operator.interfaces.Disassembler;
import io.github.createsequence.crane.core.parser.CombineOperationConfigurationParser;
import io.github.createsequence.crane.core.parser.interfaces.OperateConfigurationParser;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;
import java.util.Collection;

/**
 * 数据装卸注解
 *
 * <p>注解在嵌套字段上，将会在处理数据时将注解字段递归并展开为复数需要进行装配操作的对象。
 *
 * <p>可以通过{@link #value()}或{@link #targetClass()}指定嵌套字段的类型，
 * 当两者都为{@link Void#TYPE}时，则认为该字段为动态类型，将在处理的时候动态获取字段的第一个非{@link Collection}集合或数组的对象作为目标，
 * 然后解析配置并处理。 <br />
 * 注意，json填充暂不支持该动态类型的功能。
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
     * 待处理的字段，当注解在类属性上时该属性将强制覆盖该值
     */
    String key() default "";

    /**
     * 字段别名。
     * 仅当无法根据注解字段名找到key字段时，才尝试通过别名找到至少一个存在的字段。
     */
    String[] aliases() default {};

    /**
     * 装卸器
     */
    Class<? extends Disassembler> disassembler() default BeanReflexDisassembler.class;

    /**
     * 装卸器bean名称
     */
    String disassemblerName() default "";

    /**
     * 是否使用外层解析器
     * <ul>
     *     <li>false：将使用解析当前装卸操作的解析器解析{@link #targetClass()};</li>
     *     <li>
     *         true：将根据{@link #parser()}与{@link #parserName()}从spring容器中获取解析器，
     *         并用于解析{@link #targetClass()};
     *     </li>
     * </ul>
     *
     * @since 0.5.4
     */
    boolean useCurrParser() default true;

    /**
     * 当{@link #useCurrParser()}为false时，要使用的配置解析器
     *
     * @since 0.5.4
     */
    Class<? extends OperateConfigurationParser> parser() default CombineOperationConfigurationParser.class;

    /**
     * 当{@link #useCurrParser()}为false时，要使用的配置解析器在容器中的bean名称
     *
     * @since 0.5.4
     */
    String parserName() default "";

}
