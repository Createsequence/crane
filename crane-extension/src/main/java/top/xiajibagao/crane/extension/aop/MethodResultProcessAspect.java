package top.xiajibagao.crane.extension.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.xiajibagao.crane.extension.cache.ConfigurationCache;
import top.xiajibagao.crane.extension.helper.ConfigOptionAnnotationProcessor;
import top.xiajibagao.crane.extension.helper.ExpressionUtils;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * {@link ProcessResult}注解方法返回值处理切面
 *
 * @author huangchengxing
 * @date 2022/03/04 10:00
 */
@Slf4j
@Aspect
public class MethodResultProcessAspect extends ConfigOptionAnnotationProcessor<Method> {
    
    public MethodResultProcessAspect(BeanFactory beanFactory, ConfigurationCache configurationCache) {
        super(beanFactory, configurationCache);
    }
    
    @AfterReturning(returning = "result", pointcut = "@annotation(top.xiajibagao.crane.extension.aop.ProcessResult)")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        if (ObjectUtils.isEmpty(result)) {
            return;
        }
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        ProcessResult annotation = AnnotatedElementUtils.findMergedAnnotation(method, ProcessResult.class);
        if (Objects.isNull(annotation)) {
            return;
        }
        String condition = annotation.condition();
        // 无表达式需要执行
        if (!StringUtils.hasText(condition)) {
            process(method, result);
            return;
        }

        // 先执行表达式
        Boolean isProcess = Boolean.TRUE;
        StandardEvaluationContext context = new StandardEvaluationContext();
        ExpressionUtils.registerMethodArgs(joinPoint, methodSignature, context);
        context.setVariable("result", result);
        try {
            isProcess = ExpressionUtils.execute(condition, context, Boolean.class, true);
        } catch (Exception e) {
            log.warn("表达式[{}]执行失败，错误信息：[{}]", condition, e.getMessage());
        }
        if (Objects.nonNull(isProcess) && isProcess) {
            process(method, result);
        }
    }

}
