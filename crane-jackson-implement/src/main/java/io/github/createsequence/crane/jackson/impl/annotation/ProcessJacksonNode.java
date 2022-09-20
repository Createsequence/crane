package io.github.createsequence.crane.jackson.impl.annotation;

import io.github.createsequence.crane.core.annotation.MateAnnotation;
import io.github.createsequence.crane.core.executor.OperationExecutor;
import io.github.createsequence.crane.core.executor.UnorderedOperationExecutor;
import io.github.createsequence.crane.core.helper.DefaultGroup;
import io.github.createsequence.crane.core.parser.CombineOperationConfigurationParser;
import io.github.createsequence.crane.core.parser.interfaces.OperateConfigurationParser;

import java.lang.annotation.*;

/**
 * 表明注解对象在通过jackson序列化时，需要进行数据填充
 *
 * @author huangchengxing
 * @date 2022/04/12 17:52
 */
@MateAnnotation
@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ProcessJacksonNode {

    /**
     * 要使用的配置解析器
     */
    Class<? extends OperateConfigurationParser> parser() default CombineOperationConfigurationParser.class;

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

    /**
     * 分组
     *
     * @since 0.5.0
     */
    Class<?>[] groups() default { DefaultGroup.class };

}
