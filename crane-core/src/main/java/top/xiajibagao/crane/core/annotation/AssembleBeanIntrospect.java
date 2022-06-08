package top.xiajibagao.crane.core.annotation;

import org.springframework.core.annotation.AliasFor;
import top.xiajibagao.crane.core.container.BeanIntrospectContainer;
import top.xiajibagao.crane.core.helper.DefaultGroup;
import top.xiajibagao.crane.core.operator.BeanReflexAssembler;
import top.xiajibagao.crane.core.operator.interfaces.Assembler;

import java.lang.annotation.*;

/**
 * 字段使用{@link BeanIntrospectContainer}容器装配
 *
 * @author huangchengxing
 * @date 2022/05/13 17:49
 */
@Assemble(container = BeanIntrospectContainer.class)
@MateAnnotation
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AssembleBeanIntrospect {

    /**
     * 装配容器实例在spring中的名称
     */
    @AliasFor(annotation = Assemble.class, attribute = "containerName")
    String containerName() default "";

    /**
     * 指定容器中数据源对应的命名空间
     */
    @AliasFor(annotation = Assemble.class, attribute = "namespace")
    String namespace() default "";

    /**
     * 分组
     */
    @AliasFor(annotation = Assemble.class, attribute = "groups")
    Class<?>[] groups() default { DefaultGroup.class };

    /**
     * 指定注解的key字段别名。<br />
     * 仅当无法根据注解字段名找到key字段时，才尝试通过别名找到至少一个存在的字段。
     */
    @AliasFor(annotation = Assemble.class, attribute = "aliases")
    String[] aliases() default {};

    /**
     * 装配器
     */
    @AliasFor(annotation = Assemble.class, attribute = "assembler")
    Class<? extends Assembler> assembler() default BeanReflexAssembler.class;

    /**
     * 装配器Bean名称
     */
    @AliasFor(annotation = Assemble.class, attribute = "assemblerName")
    String assemblerName() default "";

    /**
     * 字段映射配置
     */
    @AliasFor(annotation = Assemble.class, attribute = "props")
    Prop[] props() default {};

    /**
     * 字段映射配置模板 <br />
     * 指定类型，将解析类上的{@link PropsTemplate}注解，并将其{@link PropsTemplate#value()}加入
     * 当前配置中，效果等同于直接在当前{@link #props()}配置中追加配置。
     */
    @AliasFor(annotation = Assemble.class, attribute = "propTemplates")
    Class[] propTemplates() default {};

}
