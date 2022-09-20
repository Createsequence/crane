package io.github.createsequence.crane.core.annotation;

import io.github.createsequence.crane.core.executor.OperationExecutor;
import io.github.createsequence.crane.core.executor.UnorderedOperationExecutor;
import io.github.createsequence.crane.core.helper.DefaultGroup;
import io.github.createsequence.crane.core.parser.FieldAnnotationConfigurationParser;
import io.github.createsequence.crane.core.parser.interfaces.OperateConfigurationParser;
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
     * 分组
     */
    Class<?>[] groups() default { DefaultGroup.class };

    /**
     * 要使用的配置解析器
     */
    Class<? extends OperateConfigurationParser> parser() default FieldAnnotationConfigurationParser.class;

    /**
     * 要使用的配置解析器在容器中的bean名称
     */
    String parserName() default "";

    /**
     * 要使用的执行器
     */
    Class<? extends OperationExecutor> executor() default UnorderedOperationExecutor.class;

    /**
     * 要使用的执行器在容器中的bean名称
     */
    String executorName() default "";

}
