package top.xiajibagao.crane.extension.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import top.xiajibagao.annotation.ConfigOption;
import top.xiajibagao.crane.core.executor.OperationExecutor;
import top.xiajibagao.crane.core.helper.CollUtils;
import top.xiajibagao.crane.core.operator.interfaces.OperatorFactory;
import top.xiajibagao.crane.core.parser.interfaces.OperateConfigurationParser;
import top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration;
import top.xiajibagao.crane.extension.cache.ConfigurationCache;

import java.lang.reflect.AnnotatedElement;
import java.util.Objects;

/**
 * 一个简单的{@link ConfigOption}解析器，用于快速搭建带缓存功能的注解处理器
 *
 * @param <T> 解析注解的目标类型
 * @author huangchengxing
 * @date 2022/03/06 16:49
 */
@RequiredArgsConstructor
public class ConfigOptionAnnotationProcessor<T extends AnnotatedElement> {

    private final BeanFactory beanFactory;
    private final ConfigurationCache configurationCache;
    
    @SuppressWarnings("unchecked")
    public void process(T annotatedElement, Object target) {
        ConfigOption annotation = parseAnnotation(annotatedElement);
        if (Objects.isNull(annotation)) {
            return;
        }
        Class<?> targetClass = annotation.value();
        if (targetClass.isAssignableFrom(Void.TYPE)) {
            return;
        }

        // 从缓存中获取解析器
        OperateConfigurationParser<OperationConfiguration> parser = (OperateConfigurationParser<OperationConfiguration>) beanFactory.getBean(annotation.parser());
        OperatorFactory operatorFactory = (OperatorFactory)beanFactory.getBean(annotation.operatorFactory());
        OperationConfiguration configuration = configurationCache.getOrCached(
                getNamespace(parser),
                operatorFactory.getClass(),
                targetClass,
                () -> parser.parse(targetClass, operatorFactory)
        );
        
        OperationExecutor executor = (OperationExecutor) beanFactory.getBean(annotation.executor());
        executor.execute(CollUtils.adaptToCollection(target), configuration);
    }
    
    protected String getNamespace(OperateConfigurationParser<OperationConfiguration> parser) {
        return parser.getClass().getName();
    }
    
    /**
     * 获取{@link ConfigOption}注解
     *
     * @param annotatedElement 注解元素
     * @return top.xiajibagao.crane.annotation.ProcessConfig
     * @author huangchengxing
     * @date 2022/3/6 17:02
     */
    protected ConfigOption parseAnnotation(AnnotatedElement annotatedElement) {
        return Objects.isNull(annotatedElement) ?
            null : AnnotatedElementUtils.findMergedAnnotation(annotatedElement, ConfigOption.class);
    }

}
