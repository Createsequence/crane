package io.github.createsequence.crane.core.aop;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import io.github.createsequence.crane.core.annotation.ProcessResult;
import io.github.createsequence.crane.core.cache.ConfigurationCache;
import io.github.createsequence.crane.core.helper.ConfigOptionAnnotationProcessor;
import io.github.createsequence.crane.core.helper.ExpressionUtils;
import io.github.createsequence.crane.core.helper.reflex.ReflexUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * {@link ProcessResult}注解方法返回值处理切面
 *
 * @author huangchengxing
 * @date 2022/03/04 10:00
 * @see ConfigOptionAnnotationProcessor
 */
@Slf4j
@Aspect
public class MethodResultProcessAspect extends ConfigOptionAnnotationProcessor<Method> {
    
    public MethodResultProcessAspect(BeanFactory beanFactory, ConfigurationCache configurationCache) {
        super(beanFactory, configurationCache);
    }
    
    @AfterReturning(returning = "result", pointcut = "@annotation(io.github.createsequence.crane.core.annotation.ProcessResult)")
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

        // 处理包装类
        result = getWrappedSource(result, annotation);

        // 处理表达式
        String condition = annotation.condition();
        // 无表达式需要执行
        if (CharSequenceUtil.isBlank(condition)) {
            process(method, result);
            return;
        }
        // 先执行表达式
        Boolean isProcess = Boolean.TRUE;
        StandardEvaluationContext context = new StandardEvaluationContext();
        String[] paramNames = methodSignature.getParameterNames();
        Object[] params = joinPoint.getArgs();
        ExpressionUtils.registerMethodArgs(Arrays.asList(paramNames), Arrays.asList(params), context);
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

    private Object getWrappedSource(Object data, ProcessResult annotation) {
        String wrapperIn = annotation.wrappedIn();
        if (CharSequenceUtil.isBlank(wrapperIn)) {
            return data;
        }
        Class<?> wrapperClass = data.getClass();
        Method method = ReflexUtils.findGetterMethod(wrapperClass, wrapperIn);
        if (Objects.isNull(method)) {
            method = ClassUtils.getMethod(wrapperClass, wrapperIn);
        }
        return ReflectUtil.invoke(data, method);
    }

}
