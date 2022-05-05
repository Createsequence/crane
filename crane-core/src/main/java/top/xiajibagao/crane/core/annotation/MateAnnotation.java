package top.xiajibagao.crane.core.annotation;

import java.lang.annotation.*;

/**
 * 标识性注解，表示被注解的注解是元注解，用户可以自行扩展
 *
 * @author huangchengxing
 * @date 2022/03/03 16:12
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MateAnnotation {
}
