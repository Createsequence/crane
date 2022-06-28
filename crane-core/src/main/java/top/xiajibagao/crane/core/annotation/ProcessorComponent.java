package top.xiajibagao.crane.core.annotation;

import top.xiajibagao.crane.core.operator.interfaces.OperateProcessorComponent;
import top.xiajibagao.crane.core.operator.interfaces.Operator;

import java.lang.annotation.*;

/**
 * <p>用于标记{@link OperateProcessorComponent}接口的实现类，表示该组件允许被注册到哪些分组中
 *
 * @author huangchengxing
 * @date 2022/06/27 15:03
 * @since 0.5.8
 * @see OperateProcessorComponent
 */
@MateAnnotation
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ProcessorComponent {
    
    /**
     * 表明当前组件允许被注册到哪些{@link Operator}
     *
     * @return java.lang.String[]
     * @author huangchengxing
     * @date 2022/6/27 15:11
     * @see OperateProcessorComponent#OPERATE_GROUP_JSON_BEAN
     * @see OperateProcessorComponent#OPERATE_GROUP_JAVA_BEAN
     */
    String[] value() default { OperateProcessorComponent.OPERATE_GROUP_JAVA_BEAN, OperateProcessorComponent.OPERATE_GROUP_JSON_BEAN};

}
