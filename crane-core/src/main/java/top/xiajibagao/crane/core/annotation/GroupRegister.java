package top.xiajibagao.crane.core.annotation;

import top.xiajibagao.crane.core.operator.interfaces.*;

import java.lang.annotation.*;

/**
 * <p>用于标记{@link SourceReader}，{@link SourceReadInterceptor}，{@link TargetWriter}或{@link SourceReadInterceptor}，
 * 表示该组件允许被注册到哪些{@link Operator}中
 *
 * @author huangchengxing
 * @date 2022/06/27 15:03
 * @see OperateProcessor#OPERATE_GROUP_JAVA_BEAN
 * @see OperateProcessor#OPERATE_GROUP_JSON_BEAN
 * @see 0.5.8
 */
@MateAnnotation
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GroupRegister {
    
    /**
     * 表明当前组件允许被注册到哪些{@link Operator}
     *
     * @return java.lang.String[]
     * @author huangchengxing
     * @date 2022/6/27 15:11
     * @see OperateProcessor#OPERATE_GROUP_JSON_BEAN
     * @see OperateProcessor#OPERATE_GROUP_JAVA_BEAN
     */
    String[] value() default { OperateProcessor.OPERATE_GROUP_JAVA_BEAN, OperateProcessor.OPERATE_GROUP_JSON_BEAN};

}
