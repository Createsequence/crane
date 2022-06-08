package top.xiajibagao.crane.core.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;
import java.util.Collection;

/**
 * 方法数据源对象
 * <p>注解类表明该类中存在可直接作为容器的数据源的方法，
 * 允许直接在{@link #methods()}中声明或者通过在方法上添加{@link MethodSourceBean.Method}注解的方式声明作为数据源的方法。
 *
 * @author huangchengxing
 * @date 2022/04/01 10:49
 */
@MateAnnotation(
    repeatable = true,
    used = MethodSourceBean.class,
    root = MethodSourceBean.class
)
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

    
    /**
     * 表明{@link MethodSourceBean}注解的类下作为容器的数据源的一个方法
     * <ul>
     *     <li>当直接注解在方法上时：returnType与paramTypes为非必填项；</li>
     *     <li>
     *         当直接声明在{@link MethodSourceBean#methods()}上时：
     *         若returnType与paramTypes不填，则默认寻找返回值为Collection，且有且仅有一个Collection入参的同名方法；
     *         若指定returnType与paramTypes，则根据指定返回值与入参类型寻找同名方法；
     *     </li>
     * </ul>
     *
     * @author huangchengxing 
     * @date 2022/4/12 11:19
     */
    @MateAnnotation(
        transitive = false,
        used = MethodSourceBean.Method.class,
        root = MethodSourceBean.Method.class
    )
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface Method {

        /**
         * 方法名称
         */
        String name() default "";

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
        Class<?> returnType() default Collection.class;

        /**
         * 方法的参数类型
         */
        Class[] paramTypes() default Collection.class;

        /**
         * sourceKey与数据源对象的对应关系
         */
        MappingType mappingType() default MappingType.ONE_TO_ONE;

    }

}
