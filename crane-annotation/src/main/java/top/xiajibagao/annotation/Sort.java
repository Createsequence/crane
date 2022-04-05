package top.xiajibagao.annotation;

import java.lang.annotation.*;

/**
 * <p>执行顺序，越小越靠前
 *
 * @author huangchengxing
 * @date 2022/03/05 11:38
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sort {

    int value() default Integer.MAX_VALUE;

}
