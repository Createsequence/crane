package io.github.createsequence.crane.core.annotation;

import io.github.createsequence.crane.core.executor.OperationExecutor;
import io.github.createsequence.crane.core.executor.UnorderedOperationExecutor;
import io.github.createsequence.crane.core.helper.DefaultGroup;
import io.github.createsequence.crane.core.helper.ObjectUtils;
import io.github.createsequence.crane.core.parser.CombineOperationConfigurationParser;
import io.github.createsequence.crane.core.parser.interfaces.OperateConfigurationParser;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 表示方法的返回值需要进行处理
 *
 * @author huangchengxing
 * @date 2022/03/04 11:26
 */
@MateAnnotation
@ConfigOption
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ProcessResult {

    /**
     * 待处理的目标类型，当不指定类型时，将会尝试进行自动推断
     *
     * @see ObjectUtils#getClass(Object)
     */
    @AliasFor(annotation = ConfigOption.class, attribute = "value")
    Class<?> value() default Void.class;

    /**
     * 待处理的目标类型，当不指定类型时，将会尝试进行自动推断
     *
     * @see ObjectUtils#getClass(Object)
     */
    @AliasFor(annotation = ConfigOption.class, attribute = "targetClass")
    Class<?> targetClass() default Void.class;

    /**
     * 分组
     */
    @AliasFor(annotation = ConfigOption.class, attribute = "groups")
    Class<?>[] groups() default { DefaultGroup.class };

    /**
     * 要使用的配置解析器
     */
    @AliasFor(annotation = ConfigOption.class, attribute = "parser")
    Class<? extends OperateConfigurationParser> parser() default CombineOperationConfigurationParser.class;

    /**
     * 要使用的配置解析器在容器中的bean名称
     */
    @AliasFor(annotation = ConfigOption.class, attribute = "parserName")
    String parserName() default "";

    /**
     * 要使用的执行器
     */
    @AliasFor(annotation = ConfigOption.class, attribute = "executor")
    Class<? extends OperationExecutor> executor() default UnorderedOperationExecutor.class;

    /**
     * 要使用的执行器在容器中的bean名称
     */
    @AliasFor(annotation = ConfigOption.class, attribute = "executorName")
    String executorName() default "";

    /**
     * <p>执行条件SpEL表达式
     *
     * <p>当该项不为空或空字符串时，该表达时的解析执行后必须返回布尔值true或false,当表达式解析发生异常时，将默认为true。<br />
     * 允许通过“#result”直接引用返回值，或通过“#方法参数名”引用方法参数。
     */
    String condition() default "";

    /**
     * 若方法的返回值的某个包装类，则使用该属性指定包装类中的某个值为真正需要处理的数据 <br />
     * 该属性可以填写获取数据源的方法，或者包装类中用于存放数据源的属性
     *
     * @since 0.6.0
     */
    String wrappedIn() default "";

}
