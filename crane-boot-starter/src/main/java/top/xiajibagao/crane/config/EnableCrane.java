package top.xiajibagao.crane.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用Crane框架，引入基本配置
 *
 * @author huangchengxing
 * @date 2022/03/03 13:41
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CraneAutoConfiguration.class)
public @interface EnableCrane {
}
