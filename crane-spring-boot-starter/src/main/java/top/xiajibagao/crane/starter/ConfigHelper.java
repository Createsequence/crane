package top.xiajibagao.crane.starter;

import cn.hutool.core.util.ClassUtil;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import top.xiajibagao.crane.core.annotation.GroupRegister;
import top.xiajibagao.crane.core.operator.interfaces.*;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author huangchengxing
 * @date 2022/06/27 17:39
 */
public class ConfigHelper {

    private ConfigHelper() {
    }
    
    /**
     * 向{@link OperateProcessor}注册{@link SourceReader}、{@link SourceReadInterceptor}, {@link TargetWriter}与{@link TargetWriteInterceptor}
     *
     * @param operateProcessor 操作处理器
     * @param beanFactory beanFactory
     * @param filter 过滤器
     * @author huangchengxing
     * @date 2022/6/27 18:03
     */
    public static void registerForOperateProcessor(
        OperateProcessor operateProcessor, ListableBeanFactory beanFactory, Predicate<GroupRegister> filter) {

        // 从容器中获取组件
        Collection<SourceReader> sourceReaders = beanFactory.getBeansOfType(SourceReader.class).values();
        Collection<SourceReadInterceptor> sourceReadInterceptors = beanFactory.getBeansOfType(SourceReadInterceptor.class).values();
        Collection<TargetWriter> targetWriters = beanFactory.getBeansOfType(TargetWriter.class).values();
        Collection<TargetWriteInterceptor> targetWriteInterceptors = beanFactory.getBeansOfType(TargetWriteInterceptor.class).values();

        // 注册组件
        registerTarget(sourceReaders, filter, operateProcessor::registerSourceReaders);
        registerTarget(targetWriters, filter, operateProcessor::registerTargetWriters);
        registerTarget(sourceReadInterceptors, filter, operateProcessor::registerSourceReadInterceptors);
        registerTarget(targetWriteInterceptors, filter, operateProcessor::registerTargetWriteInterceptors);
    }

    private static <T> void registerTarget(
        Collection<T> targets, Predicate<GroupRegister> filter, Consumer<T> consumer) {
        for (T target : targets) {
            Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
            if (!ClassUtil.isAssignable(OperateProcessor.class, targetClass)) {
                GroupRegister annotation = AnnotatedElementUtils.findMergedAnnotation(targetClass, GroupRegister.class);
                if (filter.test(annotation)) {
                    consumer.accept(target);
                }
            }
        }
    }

}
