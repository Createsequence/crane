package top.xiajibagao.crane.helper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import top.xiajibagao.crane.annotation.ProcessConfig;
import top.xiajibagao.crane.operator.interfaces.OperationExecutor;
import top.xiajibagao.crane.operator.interfaces.OperatorFactory;
import top.xiajibagao.crane.parse.interfaces.OperateConfigurationParser;
import top.xiajibagao.crane.parse.interfaces.OperationConfiguration;

import java.lang.reflect.AnnotatedElement;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 一个简单的{@link ProcessConfig}解析器，用于快速搭建待缓存功能的注解处理器
 *
 * @param <T> 解析注解的目标类型
 * @author huangchengxing
 * @date 2022/03/06 16:49
 */
@RequiredArgsConstructor
public abstract class CacheableAnnotationProcessor<T extends AnnotatedElement> {

    private final BeanFactory beanFactory;

    private final Map<String, Processor> processorCache = new ConcurrentHashMap<>();

    public void process(T annotatedElement, Object target) {
        ProcessConfig annotation = parseAnnotation(annotatedElement);
        if (Objects.isNull(annotation)) {
            return;
        }
        Class<?> targetClass = annotation.value();
        if (targetClass.isAssignableFrom(Void.TYPE)) {
            return;
        }

        String processorId = getProcessorId(annotatedElement, target, annotation);
        Processor processor = getProcessor(processorId, annotation);
        execute(target, processor);
    }
    
    /**
     * 从缓存中获取处理器，若不存在则先进行初始化
     *
     * @param processorId 处理器id
     * @param annotation 注解
     * @return top.xiajibagao.crane.helper.CacheableAnnotationProcessor.Processor
     * @author huangchengxing
     * @date 2022/3/6 17:05
     */
    protected Processor getProcessor(String processorId, ProcessConfig annotation) {
        return processorCache.computeIfAbsent(processorId, id -> {
            OperatorFactory operatorFactory = (OperatorFactory)beanFactory.getBean(annotation.operatorFactory());
            OperateConfigurationParser<?> parser = (OperateConfigurationParser<?>)beanFactory.getBean(annotation.parser());
            // 缓存解析信息
            return new CacheableAnnotationProcessor.Processor(
                id,
                (OperationExecutor) beanFactory.getBean(annotation.executor()),
                parser.parse(annotation.value(), operatorFactory)
            );
        });
    }

    /**
     * 执行
     *
     * @param target 目标实例
     * @param processor 处理器
     * @author huangchengxing
     * @date 2022/3/6 17:03
     */
    protected void execute(Object target, Processor processor) {
        processor.execute(target);
    }
    
    /**
     * 获取{@link ProcessConfig}注解
     *
     * @param annotatedElement 注解元素
     * @return top.xiajibagao.crane.annotation.ProcessConfig
     * @author huangchengxing
     * @date 2022/3/6 17:02
     */
    protected ProcessConfig parseAnnotation(AnnotatedElement annotatedElement) {
        return Objects.isNull(annotatedElement) ?
            null : AnnotatedElementUtils.findMergedAnnotation(annotatedElement, ProcessConfig.class);
    }

    /**
     * 获取处理器id
     *
     * @param annotatedElement 注解元素
     * @param target 待处理器对象
     * @param annotation 注解
     * @return java.lang.String
     * @author huangchengxing
     * @date 2022/3/6 16:56
     */
    protected abstract String getProcessorId(T annotatedElement, Object target, ProcessConfig annotation);

    /**
     * 配置与执行器缓存
     *
     * @author huangchengxing
     * @date 2022/03/06 16:47
     */
    @Getter
    @RequiredArgsConstructor
    public static class Processor {
        private final String id;
        private final OperationExecutor executor;
        private final OperationConfiguration configuration;

        public void execute(Object target) {
            executor.execute(CollUtils.adaptToCollection(target), configuration);
        }

    }

    public static class SimpleCacheableAnnotationProcessor<T extends AnnotatedElement> extends CacheableAnnotationProcessor<T> {

        private final Function<T, String> idMapper;

        public SimpleCacheableAnnotationProcessor(BeanFactory beanFactory, Function<T, String> idMapper) {
            super(beanFactory);
            this.idMapper = idMapper;
        }

        @Override
        protected String getProcessorId(T annotatedElement, Object target, ProcessConfig annotation) {
            return idMapper.apply(annotatedElement);
        }

    }

}
