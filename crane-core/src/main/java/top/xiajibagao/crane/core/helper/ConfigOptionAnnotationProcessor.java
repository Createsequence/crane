package top.xiajibagao.crane.core.helper;

import cn.hutool.core.util.ArrayUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import top.xiajibagao.crane.core.annotation.ConfigOption;
import top.xiajibagao.crane.core.cache.ConfigurationCache;
import top.xiajibagao.crane.core.executor.OperationExecutor;
import top.xiajibagao.crane.core.parser.interfaces.OperateConfigurationParser;
import top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration;

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
    public void process(T annotatedElement, Object target, Class<?>... targetGroups) {
        ConfigOption annotation = parseAnnotation(annotatedElement);
        if (Objects.isNull(annotation) || ArrayUtil.isEmpty(targetGroups)) {
            return;
        }
        Class<?> targetClass = getTargetClass(annotation, annotatedElement, target);
        if (targetClass.isAssignableFrom(Void.TYPE)) {
            return;
        }

        // 从缓存中获取解析器
        OperateConfigurationParser parser = BeanFactoryUtils.getBean(beanFactory, annotation.parser(), annotation.parserName());
        OperationConfiguration configuration = configurationCache.getOrCached(
            getNamespace(parser), targetClass, parser::parse
        );
        OperationExecutor executor = BeanFactoryUtils.getBean(beanFactory, annotation.executor(), annotation.executorName());
        executor.execute(CollUtils.adaptToCollection(target), configuration, targetGroups);
    }
    
    /**
     * 获取指定namespace
     *
     * @param parser 解析器
     * @return java.lang.String
     * @author huangchengxing
     * @date 2022/5/5 22:56
     */
    protected String getNamespace(OperateConfigurationParser parser) {
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
    
    /**
     * 获取配置对象类型
     *
     * @param annotation 注解
     * @param annotatedElement 注解元素
     * @param target 待处理对象
     * @return java.lang.Class<?> 指定的配置对象类型，当返回值类型为{@link Void}时则跳过本次操作
     * @author huangchengxing
     * @date 2022/5/5 22:51
     */
    protected Class<?> getTargetClass(ConfigOption annotation, T annotatedElement, Object target) {
        return annotation.value();
    }

}
