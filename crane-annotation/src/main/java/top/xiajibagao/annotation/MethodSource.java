package top.xiajibagao.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;
import java.util.Collection;

/**
 * 标记一个入参有且仅有一个{@link Collection}类型，且返回值也为{@link Collection}类型的方法，
 * 将允许将该方法作为容器数据源。当引用时，通过在{@link Assemble}注解中通过namespace与name对应。
 *
 * @author huangchengxing
 * @date 2022/4/1 17:13
 */
@MateAnnotation
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MethodSource {

    /**
     * 容器名称
     */
    @AliasFor("namespace")
    String value() default "";

    /**
     * 容器名称
     */
    @AliasFor("value")
    String namespace() default "";

    /**
     * 数据源对象类型
     */
    Class<?> sourceType() default Object.class;

    /**
     * 获取的数据源key字段
     */
    String sourceKey() default "";

    /**
     * sourceKey与数据源对象的对应关系
     */
    MappingType mappingType() default MappingType.ONE_TO_ONE;

}
