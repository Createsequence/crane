package top.xiajibagao.crane.impl.json.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.annotation.AliasFor;
import top.xiajibagao.crane.annotation.MateAnnotation;
import top.xiajibagao.crane.annotation.ProcessConfig;
import top.xiajibagao.crane.impl.json.JacksonOperatorFactory;
import top.xiajibagao.crane.operator.UnorderedOperationExecutor;
import top.xiajibagao.crane.operator.interfaces.OperationExecutor;
import top.xiajibagao.crane.operator.interfaces.OperatorFactory;
import top.xiajibagao.crane.parse.BeanOperateConfigurationParser;
import top.xiajibagao.crane.parse.interfaces.OperateConfigurationParser;

import java.lang.annotation.*;

/**
 * 表示json允许在注册了{@link CraneDynamicJsonModule}的{@link ObjectMapper}中进行处理
 *
 * @author huangchengxing
 * @date 2022/03/06 16:42
 */
@ProcessConfig
@MateAnnotation
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ProcessJson {

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
    @AliasFor(annotation = ProcessConfig.class)
    Class<? extends OperateConfigurationParser<?>> parser() default BeanOperateConfigurationParser.class;

    /**
     * 要使用的操作者工厂
     */
    @AliasFor(annotation = ProcessConfig.class)
    Class<? extends OperatorFactory> operatorFactory() default JacksonOperatorFactory.class;

    /**
     * 要使用的执行器
     */
    @AliasFor(annotation = ProcessConfig.class)
    Class<? extends OperationExecutor> executor() default UnorderedOperationExecutor.class;

}
