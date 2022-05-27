package top.xiajibagao.crane.core.annotation;

import org.springframework.core.annotation.AliasFor;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandlerChain;

import java.lang.annotation.*;

/**
 * 操作处理器标记枚举，通过分组、处理器链类型与处理器链在容器中的bean名称，
 * 区分该操作处理器需要注册到哪些处理器链实例。
 *
 * @author huangchengxing
 * @date 2022/05/27 16:54
 */
@MateAnnotation
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RegisterFor {

    /**
     * 声明该处理器属于哪一分组
     */
    @AliasFor("group")
    Class<?> value() default Void.class;

    /**
     * 声明该处理器属于哪一分组
     */
    @AliasFor("value")
    Class<?> group() default Void.class;

    /**
     * 指定该处理器要注册到哪些类型的处理器链中
     */
    Class<? extends OperateHandlerChain>[] registerChainTypes() default {};

    /**
     * 指定该处理器要注册到哪些类型的处理器链中
     */
    String[] registerChainNames() default {};

}
