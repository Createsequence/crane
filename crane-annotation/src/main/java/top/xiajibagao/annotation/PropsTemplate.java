package top.xiajibagao.annotation;

import java.lang.annotation.*;

/**
 * <p>字段映射配置模板
 *
 * <p>在Assemble中指定被注解的类型，
 * 将从类上的注解中解析{@link #value()}并追加到props中。<br />
 * <b>注意：若模板类注解中字段与实际类属性注解配置相同，则先执行属性注解配置，再执行模板配置</b>
 *
 * @author huangchengxing
 * @date 2022/03/03 14:51
 */
@MateAnnotation
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PropsTemplate {
    Prop[] value() default {};
}
