package top.xiajibagao.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 表示该类实例下存在可作为数据源的方法
 *
 * @author huangchengxing
 * @date 2022/04/01 10:49
 */
@MateAnnotation
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MethodSourceBean {

    /**
     * 容器方法
     */
    @AliasFor("methods")
    Method[] value() default {};

    /**
     * 容器方法
     */
    @AliasFor("value")
    Method[] methods() default {};

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface Method {

        /**
         * 容器名称
         */
        String name();

        /**
         * 容器名称
         */
        String namespace();

        /**
         * 数据源对象类型
         */
        Class<?> sourceType();

        /**
         * 获取的数据源key字段
         */
        String sourceKey();

        /**
         * 方法的返回值类型
         */
        Class<?> returnType();

        /**
         * 方法的参数类型
         */
        Class[] paramTypes() default {};

    }

}
