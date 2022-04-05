package top.xiajibagao.crane.extension.aop;

import org.springframework.core.annotation.AliasFor;
import top.xiajibagao.annotation.ConfigOption;
import top.xiajibagao.annotation.MateAnnotation;
import top.xiajibagao.crane.core.executor.OperationExecutor;
import top.xiajibagao.crane.core.executor.UnorderedOperationExecutor;
import top.xiajibagao.crane.core.operator.BeanReflexOperatorFactory;
import top.xiajibagao.crane.core.operator.interfaces.OperatorFactory;
import top.xiajibagao.crane.core.parser.BeanOperateConfigurationParser;
import top.xiajibagao.crane.core.parser.interfaces.OperateConfigurationParser;

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
    @AliasFor(annotation = ConfigOption.class)
    Class<? extends OperateConfigurationParser<?>> parser() default BeanOperateConfigurationParser.class;

    /**
     * 要使用的操作者工厂
     */
    @AliasFor(annotation = ConfigOption.class)
    Class<? extends OperatorFactory> operatorFactory() default BeanReflexOperatorFactory.class;

    /**
     * 要使用的执行器
     */
    @AliasFor(annotation = ConfigOption.class)
    Class<? extends OperationExecutor> executor() default UnorderedOperationExecutor.class;

    /**
     * <p>执行条件SpEL表达式
     *
     * <p>当该项不为空或空字符串时，该表达时的解析执行后必须返回布尔值true或false,当表达式解析发生异常时，将默认为true。<br />
     * 允许通过“#result”直接引用返回值，或通过“#方法参数名”引用方法参数。
     */
    String condition() default "";

}
