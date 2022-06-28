package top.xiajibagao.crane.core.annotation;

import top.xiajibagao.crane.core.operator.interfaces.GroupRegistrable;
import top.xiajibagao.crane.core.operator.interfaces.Operator;

import java.lang.annotation.*;

/**
 * <p>用于标记{@link GroupRegistrable}接口的实现类，表示该组件允许被注册到哪些分组中
 *
 * @author huangchengxing
 * @date 2022/06/27 15:03
 * @since 0.5.8
 * @see GroupRegistrable
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
     * @see GroupRegistrable#OPERATE_GROUP_JSON_BEAN
     * @see GroupRegistrable#OPERATE_GROUP_JAVA_BEAN
     */
    String[] value() default { GroupRegistrable.OPERATE_GROUP_JAVA_BEAN, GroupRegistrable.OPERATE_GROUP_JSON_BEAN};

}
