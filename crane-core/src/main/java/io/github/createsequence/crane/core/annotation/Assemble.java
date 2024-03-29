package io.github.createsequence.crane.core.annotation;

import io.github.createsequence.crane.core.container.Container;
import io.github.createsequence.crane.core.container.KeyValueContainer;
import io.github.createsequence.crane.core.helper.DefaultGroup;
import io.github.createsequence.crane.core.operator.BeanReflexAssembler;
import io.github.createsequence.crane.core.operator.interfaces.Assembler;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 数据装配注解
 * <p>注解在key字段上，将从指定的Container以当前注解字段值与{@link #namespace()}获取数据源并填充到当前对象。
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
     * 本次装配操作对应的key字段，当注解在类属性上时，该属性将强制覆盖该值
     */
    @AliasFor("key")
    String value() default "";

    /**
     * 本次装配操作对应的key字段，当注解在类属性上时，该属性将强制覆盖该值
     */
    @AliasFor("value")
    String key() default "";

    /**
     * 指定完成装配操作的容器，该实例需要可以用spring容器获取
     */
    Class<? extends Container> container() default KeyValueContainer.class;

    /**
     * 装配容器实例在spring中的名称，当{@link #value()}或{@link #container()}有值时将用于一起查找容器
     */
    String containerName() default "";

    /**
     * 装配器
     */
    Class<? extends Assembler> assembler() default BeanReflexAssembler.class;

    /**
     * 装配器Bean名称
     */
    String assemblerName() default "";

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
     * 分组
     *
     * @since 0.5.0
     */
    Class<?>[] groups() default { DefaultGroup.class };

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
