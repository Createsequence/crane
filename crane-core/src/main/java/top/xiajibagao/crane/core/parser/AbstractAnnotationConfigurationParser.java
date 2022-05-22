package top.xiajibagao.crane.core.parser;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
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
import top.xiajibagao.crane.core.helper.BeanFactoryUtils;
import top.xiajibagao.crane.core.helper.CollUtils;
import top.xiajibagao.crane.core.helper.ObjectUtils;
import top.xiajibagao.crane.core.parser.interfaces.*;

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
public abstract class AbstractAnnotationConfigurationParser<T extends OperationConfiguration> implements OperateConfigurationParser<T> {

    protected final GlobalConfiguration globalConfiguration;
    protected final BeanFactory beanFactory;

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
    protected AssembleOperation createAssembleAnnotation(Field key, Assemble annotation, BeanOperationConfiguration configuration) {
        // 解析属性配置
        List<AssembleProperty> properties = new ArrayList<>(CollStreamUtil.toList(
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
            CollUtils.toSet(Arrays.asList(annotation.aliases())),
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
     * @return java.util.List<AssembleProperty>
     * @author huangchengxing
     * @date 2022/3/3 15:05
     */
    protected List<AssembleProperty> parsePropsTemplateAnnotation(Class<?> targetClass) {
        PropsTemplate annotation = AnnotatedElementUtils.findMergedAnnotation(targetClass, PropsTemplate.class);
        if (Objects.isNull(annotation)) {
            return Collections.emptyList();
        }
        return Stream.of(annotation.value())
            .map(this::parsePropAnnotation)
            .collect(Collectors.toList());
    }

    /**
     * 解析{@link Prop}注解
     *
     * @param annotation 注解
     * @return top.xiajibagao.crane.core.parser.interfaces.AssembleProperty
     * @author huangchengxing
     * @date 2022/5/21 18:00
     */
    protected AssembleProperty parsePropAnnotation(Prop annotation) {
        return new BeanAssembleProperty(annotation.value(), annotation.src(), annotation.exp(), annotation.expType());
    }

    /**
     * 根据{@link Disassemble}注解创建{@link DisassembleOperation}
     *
     * @param key 属性
     * @param annotation {@link Disassemble}注解
     * @param configuration 当前正在构建的配置
     * @param operationConfiguration 待拆卸属性的类型配置
     * @return DisassembleOperation
     * @author huangchengxing
     * @date 2022/3/1 17:50
     */
    protected DisassembleOperation createDisassembleOperation(Field key, Disassemble annotation, BeanOperationConfiguration configuration, OperationConfiguration operationConfiguration) {
        return new BeanDisassembleOperation(
            ObjectUtils.computeIfNotNull(
                AnnotatedElementUtils.getMergedAnnotation(key, Order.class), Order::value, Ordered.LOWEST_PRECEDENCE
            ),
            configuration,
            BeanFactoryUtils.getBean(beanFactory, annotation.disassembler(), annotation.disassemblerName()),
            operationConfiguration,
            key,
            CollUtils.toSet(Arrays.asList(annotation.aliases()))
        );
    }

}
