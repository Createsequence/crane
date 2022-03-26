package top.xiajibagao.crane.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import top.xiajibagao.crane.annotation.ProcessConfig;
import top.xiajibagao.crane.extend.cache.CacheableParserWrapper;
import top.xiajibagao.crane.extend.cache.ICacheManager;
import top.xiajibagao.crane.operator.interfaces.OperationExecutor;
import top.xiajibagao.crane.operator.interfaces.OperatorFactory;
import top.xiajibagao.crane.parse.interfaces.OperateConfigurationParser;
import top.xiajibagao.crane.parse.interfaces.OperationConfiguration;

import java.lang.reflect.AnnotatedElement;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一个简单的{@link ProcessConfig}解析器，用于快速搭建带缓存功能的注解处理器
 *
 * @param <T> 解析注解的目标类型
 * @author huangchengxing
 * @date 2022/03/06 16:49
 */
@RequiredArgsConstructor
public class CacheableAnnotationProcessor<T extends AnnotatedElement> {

    private final BeanFactory beanFactory;
    private final Map<Class<OperateConfigurationParser<OperationConfiguration>>, OperateConfigurationParser<OperationConfiguration>> parseCaches = new ConcurrentHashMap<>();
    private final ICacheManager cacheManager;

    public void process(T annotatedElement, Object target) {
        ProcessConfig annotation = parseAnnotation(annotatedElement);
        if (Objects.isNull(annotation)) {
            return;
        }
        Class<?> targetClass = annotation.value();
        if (targetClass.isAssignableFrom(Void.TYPE)) {
            return;
        }

        // 从缓存中获取解析器
        OperateConfigurationParser<OperationConfiguration> parser = getCacheableParser(annotation.parser());
        OperatorFactory operatorFactory = (OperatorFactory)beanFactory.getBean(annotation.operatorFactory());
        OperationExecutor executor = (OperationExecutor) beanFactory.getBean(annotation.executor());
        executor.execute(CollUtils.adaptToCollection(target), parser.parse(annotation.targetClass(), operatorFactory));
    }

    /**
     * 从缓存中获取解析器，若解析器不存在，则先获取解析器，将其包装为带配置缓存功能的解析器后再加入当前解析器缓存
     *
     * @param parserClass 获取的解析器类型
     * @return cn.net.nova.crane.api.parse.OperateConfigurationParser<cn.net.nova.crane.api.parse.OperationConfiguration>
     * @author huangchengxing
     * @date 2022/3/26 14:26
     */
    @SuppressWarnings("unchecked")
    protected OperateConfigurationParser<OperationConfiguration> getCacheableParser(Class<?> parserClass) {
        Assert.isTrue(ClassUtils.isAssignable(OperateConfigurationParser.class, parserClass), "未知的解析器：" + parserClass);
        Class<OperateConfigurationParser<OperationConfiguration>> typedClass = (Class<OperateConfigurationParser<OperationConfiguration>>)parserClass;
        OperateConfigurationParser<OperationConfiguration> parser = beanFactory.getBean(typedClass);
        return parseCaches.computeIfAbsent(
            typedClass, t -> ClassUtils.isAssignable(CacheableParserWrapper.class, typedClass) ?
                parser : new CacheableParserWrapper<>(cacheManager, parser)
        );
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

}
