package top.xiajibagao.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * <p>数据装配注解
 *
 * <p>注解在key字段上，将从指定的Container以当前注解字段值与{@link #namespace()}获取数据源并填充到当前对象。
 * <ul>
 *     <li>未配置{@link #props()}或{@link #propTemplates()}时，总是尝试将数据源直接填入注解字段；</li>
 *     <li>
 *         配置{@link #props()}或{@link #propTemplates()}，但是未设置{@link Prop#src()}时，
 *         则尝试将数据源直接填入{@link Prop#value()}指定字段；
 *     </li>
 *     <li>
 *         配置{@link #props()}或{@link #propTemplates()}，且设置{@link Prop#src()}时:<br />
 *         1.若数据源不为对象，则直接将数据源填入填入{@link Prop#value()}指定字段；<br />
 *         2.数据源为对象，则将尝试获取数据源对应字段并填入{@link Prop#value()}指定字段；
 *     </li>
 * </ul>
 *
 * @author huangchengxing
 * @date 2022/02/28 18:00
 */
@MateAnnotation
@Repeatable(Assemble.List.class)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Assemble {

    /**
     * 指定用于提供数据源的容器，该实例需要可以用spring容器获取
     */
    @AliasFor("container")
    Class<?> value() default Void.class;

    /**
     * 指定用于提供数据源的容器，该实例需要可以用spring容器获取
     */
    @AliasFor("value")
    Class<?> container() default Void.class;

    /**
     * 容器实例在spring中的名称，当{@link #value()}或{@link #container()}有值时将用于一起查找容器
     */
    String containerName() default "";

    /**
     * 指定容器中数据源对应的命名空间
     */
    String namespace() default "";

    /**
     * 指定注解的key字段别名。<br />
     * 仅当无法根据注解字段名找到key字段时，才尝试通过别名找到至少一个存在的字段。
     */
    String[] aliases() default {};

    /**
     * 字段映射配置
     */
    Prop[] props() default {};

    /**
     * 字段映射配置模板 <br />
     * 指定类型，将解析类上的{@link PropsTemplate}注解，并将其{@link PropsTemplate#value()}加入
     * 当前配置中，效果等同于直接在当前{@link #props()}配置中追加配置。
     */
    Class[] propTemplates() default {};

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        Assemble[] value() default {};
    }

}
