package top.xiajibagao.crane.impl.bean.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.ObjectUtils;
import top.xiajibagao.crane.annotation.ProcessConfig;
import top.xiajibagao.crane.helper.CacheableAnnotationProcessor;

import java.lang.reflect.Method;

/**
 * {@link ProcessResult}注解方法返回值处理切面
 *
 * @author huangchengxing
 * @date 2022/03/04 10:00
 */
@Slf4j
@Aspect
public class MethodResultProcessAspect extends CacheableAnnotationProcessor<Method> {

    public MethodResultProcessAspect(BeanFactory beanFactory) {
        super(beanFactory);
    }

    @AfterReturning(returning = "result", pointcut = "@annotation(top.xiajibagao.crane.impl.bean.aop.ProcessResult)")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        if (ObjectUtils.isEmpty(result)) {
            return;
        }
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        process(method, result);
    }

    @Override
    protected String getProcessorId(Method annotatedElement, Object target, ProcessConfig annotation) {
        return annotatedElement.getName();
    }

}
