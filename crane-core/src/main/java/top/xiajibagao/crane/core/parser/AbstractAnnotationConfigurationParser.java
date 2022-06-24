package top.xiajibagao.crane.core.parser;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;
import top.xiajibagao.crane.core.annotation.Assemble;
import top.xiajibagao.crane.core.annotation.Disassemble;
import top.xiajibagao.crane.core.annotation.Prop;
import top.xiajibagao.crane.core.annotation.PropsTemplate;
import top.xiajibagao.crane.core.cache.OperationConfigurationCache;
import top.xiajibagao.crane.core.helper.BeanFactoryUtils;
import top.xiajibagao.crane.core.helper.CollUtils;
import top.xiajibagao.crane.core.helper.ObjectUtils;
import top.xiajibagao.crane.core.parser.interfaces.*;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author huangchengxing
 * @date 2022/05/22 15:05
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractAnnotationConfigurationParser implements OperateConfigurationParser {

    protected final GlobalConfiguration globalConfiguration;
    protected final BeanFactory beanFactory;

    @Nonnull
    @Override
    public OperationConfiguration parse(Class<?> targetClass) {
        return OperateConfigurationParser.isNotParseClass(targetClass) ?
            createConfiguration(targetClass) : parse(targetClass, new ParseContext());
    }
    
    /**
     * 基于当前上下文解析目标类的操作配置
     *
     * @param targetClass 目标类型
     * @param parseContext 解析上下文
     * @return T
     * @author huangchengxing
     * @date 2022/5/22 17:08
     */
    @Nonnull
    protected abstract OperationConfiguration parse(Class<?> targetClass, ParseContext parseContext);

    /**
     * 创建一个操作配置
     *
     * @param targetClass 目标类型
     * @return top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration
     * @author huangchengxing
     * @date 2022/6/24 16:08
     * @since 0.5.7
     */
    @Nonnull
    protected abstract OperationConfiguration createConfiguration(Class<?> targetClass);

    /**
     * 根据{@link Assemble}注解创建{@link AssembleOperation}
     *
     * @param key 属性
     * @param annotation 注解
     * @param configuration 配置
     * @return AssembleOperation
     * @author huangchengxing
     * @date 2022/3/1 17:14
     */
    protected AssembleOperation createAssembleOperation(Field key, Assemble annotation, OperationConfiguration configuration) {
        // 解析属性配置
        List<PropertyMapping> properties = new ArrayList<>(CollStreamUtil.toList(
            Arrays.asList(annotation.props()), this::parsePropAnnotation)
        );
        // 若存在属性模板，则解析模板，并将属性配置加入当前配置
        Stream.of(annotation.propTemplates())
            .map(this::parsePropsTemplateAnnotation)
            .filter(CollUtil::isNotEmpty)
            .flatMap(Collection::stream)
            .forEach(properties::add);

        return new BeanAssembleOperation(
            ObjectUtils.computeIfNotNull(
                AnnotatedElementUtils.getMergedAnnotation(key, Order.class), Order::value, Ordered.LOWEST_PRECEDENCE
            ),
            configuration,
            key,
            CollUtils.toSet(annotation.aliases()),
            annotation.namespace(),
            BeanFactoryUtils.getBean(beanFactory, annotation.container(), annotation.containerName()),
            BeanFactoryUtils.getBean(beanFactory, annotation.assembler(), annotation.assemblerName()),
            properties,
            CollUtils.toSet(annotation.groups())
        );
    }

    /**
     * 解析{@link PropsTemplate}注解
     *
     * @param targetClass 目标类型
     * @return java.util.List<PropertyMapping>
     * @author huangchengxing
     * @date 2022/3/3 15:05
     */
    protected List<PropertyMapping> parsePropsTemplateAnnotation(Class<?> targetClass) {
        return AnnotatedElementUtils.findAllMergedAnnotations(targetClass, PropsTemplate.class).stream()
            .filter(Objects::nonNull)
            .map(PropsTemplate::value)
            .filter(ArrayUtil::isNotEmpty)
            .flatMap(Stream::of)
            .map(this::parsePropAnnotation)
            .collect(Collectors.toList());
    }

    /**
     * 解析{@link Prop}注解
     *
     * @param annotation 注解
     * @return top.xiajibagao.crane.core.parser.interfaces.PropertyMapping
     * @author huangchengxing
     * @date 2022/5/21 18:00
     */
    protected PropertyMapping parsePropAnnotation(Prop annotation) {
        return new BeanPropertyMapping(annotation.value(), annotation.src(), annotation.exp(), annotation.expType());
    }

    /**
     * 根据{@link Disassemble}注解创建{@link DisassembleOperation}
     *
     * @param key 属性
     * @param annotation {@link Disassemble}注解
     * @param configuration 当前正在构建的配置
     * @param disassembleConfiguration 待装卸属性的类型配置
     * @return DisassembleOperation
     * @author huangchengxing
     * @date 2022/3/1 17:50
     */
    protected DisassembleOperation createDisassembleOperation(
        Field key, Disassemble annotation, OperationConfiguration configuration, OperationConfiguration disassembleConfiguration) {
        return new BeanDisassembleOperation(
            getActualOrder(),
            configuration,
            BeanFactoryUtils.getBean(beanFactory, annotation.disassembler(), annotation.disassemblerName()),
            disassembleConfiguration,
            key,
            CollUtils.toSet(annotation.aliases())
        );
    }

    /**
     * 根据{@link Disassemble}注解创建{@link DynamicDisassembleOperation}
     *
     * @param parser 解析器
     * @param key 属性
     * @param annotation {@link Disassemble}注解
     * @param configuration 当前正在构建的配置
     * @return DisassembleOperation
     * @author huangchengxing
     * @date 2022/3/1 17:50
     */
    protected DynamicDisassembleOperation createDynamicDisassembleOperation(
        OperateConfigurationParser parser, Field key, Disassemble annotation, OperationConfiguration configuration) {
        return new BeanDynamicDisassembleOperation(
            parser,
            getActualOrder(),
            configuration,
            BeanFactoryUtils.getBean(beanFactory, annotation.disassembler(), annotation.disassemblerName()),
            key, CollUtils.toSet(annotation.aliases()),
            beanFactory.getBean(OperationConfigurationCache.class)
        );
    }

    protected OperateConfigurationParser getDisassembleOperationParser(Disassemble annotation) {
        return annotation.useCurrParser() ?
            this : BeanFactoryUtils.getBean(beanFactory, annotation.parser(), annotation.parserName());
    }

    /**
     * 解析上下文，用于缓存一次解析操作中涉及到的配置类，以处理循环依赖问题
     *
     * @author huangchengxing 
     * @date 2022/5/22 17:11
     */
    @RequiredArgsConstructor
    protected static class ParseContext {

        private final Map<Class<?>, OperationConfiguration> lookingForConfigurations;
        private final Set<Class<?>> excluded = new HashSet<>();

        public ParseContext() {
            this(new HashMap<>(4));
        }

        public boolean isInLooking(Class<?> targetClass) {
            return lookingForConfigurations.containsKey(targetClass);
        }

        public void looking(Class<?> targetClass, OperationConfiguration configuration) {
            lookingForConfigurations.put(targetClass, configuration);
        }

        public OperationConfiguration get(Class<?> targetClass) {
            return lookingForConfigurations.get(targetClass);
        }

        public void exclude(Class<?>... excludeClass) {
            CollUtil.addAll(excluded, excludeClass);
        }

        public boolean isExcluded(Class<?> targetClass) {
            return excluded.contains(targetClass);
        }

    }

}
