package io.github.createsequence.crane.core.parser;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import io.github.createsequence.crane.core.annotation.Assemble;
import io.github.createsequence.crane.core.annotation.Disassemble;
import io.github.createsequence.crane.core.exception.CraneException;
import io.github.createsequence.crane.core.helper.BeanFactoryUtils;
import io.github.createsequence.crane.core.helper.Orderly;
import io.github.createsequence.crane.core.helper.reflex.ReflexUtils;
import io.github.createsequence.crane.core.parser.interfaces.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 解析类属性中的{@link Assemble}与{@link Disassemble}注解，生成配置类
 *
 * @author huangchengxing
 * @date 2022/03/01 16:13
 * @since 0.5.0
 */
@Slf4j
public class FieldAnnotationConfigurationParser
    extends AbstractAnnotationConfigurationParser
    implements OperateConfigurationParser {

    public FieldAnnotationConfigurationParser(GlobalConfiguration globalConfiguration, BeanFactory beanFactory) {
        super(globalConfiguration, beanFactory);
    }

    @Nonnull
    @Override
    protected OperationConfiguration parse(Class<?> targetClass, ParseContext parseContext) {
        OperationConfiguration operationConfiguration = createConfiguration(targetClass);
        List<AssembleOperation> assembleOperations = new ArrayList<>();
        List<DisassembleOperation> disassembleOperations = new ArrayList<>();
        // 解析属性注解获取操作配置
        ReflexUtils.forEachFromClass(targetClass, Class::getDeclaredFields, f -> {
            assembleOperations.addAll(parseAssembleAnnotationOnField(f, operationConfiguration));
            disassembleOperations.addAll(parseDisassembleAnnotationOnField(f, operationConfiguration, parseContext));
        });
        // 排序
        operationConfiguration.getAssembleOperations().addAll(CollUtil.sort(assembleOperations, Orderly::compareTo));
        operationConfiguration.getDisassembleOperations().addAll(CollUtil.sort(disassembleOperations, Orderly::compareTo));
        return operationConfiguration;
    }

    /**
     * 创建一个配置
     *
     * @param targetClass 目标类型
     * @return T
     * @author huangchengxing
     * @date 2022/3/1 16:24
     */
    @Override
    @Nonnull
    protected OperationConfiguration createConfiguration(Class<?> targetClass) {
        return new BeanOperationConfiguration(globalConfiguration, targetClass, new ArrayList<>(), new ArrayList<>());
    }

    // =========================== 解析属性上的装配注解 ===========================

    /**
     * 解析属性上的{@link Assemble}注解
     *
     * @param key 属性
     * @param configuration 配置
     * @return java.util.List<AssembleOperation>
     * @author huangchengxing
     * @date 2022/3/1 16:55
     */
    protected List<AssembleOperation> parseAssembleAnnotationOnField(Field key, OperationConfiguration configuration) {
        // 获取全部Assemble注解，包括正常注解，重复注解与元注解
        List<Assemble> annotations = AnnotatedElementUtils.getAllMergedAnnotations(key, Assemble.List.class).stream()
            .map(Assemble.List::value)
            .flatMap(Stream::of)
            .collect(Collectors.toList());
        annotations.addAll(AnnotatedElementUtils.getAllMergedAnnotations(key, Assemble.class));
        // 校验字段是否同时是装配和装卸字段
        CraneException.throwIfFalse(
            CollectionUtils.isEmpty(annotations) || !AnnotatedElementUtils.hasAnnotation(key, Disassemble.class),
            "属性[{}]无法同时被{}和{}注解标记",
            configuration.getTargetClass(), key, Assemble.class, Disassemble.class
        );
        return CollStreamUtil.toList(annotations, a -> createAssembleOperation(key, a, configuration));
    }
    
    // =========================== 解析属性上的装卸注解 ===========================
    
    /**
     * 解析属性上的{@link Disassemble}注解
     *
     * @param key 属性
     * @param configuration 当前正在构建的配置
     * @param parseContext 解析上下文，用于缓存配置处理循环依赖
     * @return java.util.List<DisassembleOperation>
     * @author huangchengxing
     * @date 2022/3/1 17:49
     */
    protected List<DisassembleOperation> parseDisassembleAnnotationOnField(Field key, OperationConfiguration configuration, ParseContext parseContext) {
        Set<Disassemble> disassembles = AnnotatedElementUtils.findAllMergedAnnotations(key, Disassemble.class);
        if (CollUtil.isEmpty(disassembles)) {
            return Collections.emptyList();
        }
        CraneException.throwIfTrue(disassembles.size() > 1, "属性[{}]不允许被多个{}注解！", key, Disassemble.class);
        Disassemble annotation = CollUtil.getFirst(disassembles);
        CraneException.throwIfTrue(
            AnnotatedElementUtils.hasAnnotation(key, Assemble.class),
            "属性[{}]无法同时被{}和{}注解标记", configuration.getTargetClass(), key, Assemble.class, Disassemble.class
        );

        // 递归解析拆卸字段类型
        parseContext.looking(configuration.getTargetClass(), configuration);

        // 若不指定类型，则认为其为动态类型
        Class<?> disassembleType = annotation.value();
        DisassembleOperation operation;
        if (Objects.equals(Void.class, disassembleType)) {
            OperateConfigurationParser parser = getDisassembleOperationParser(annotation);
            operation = createDynamicDisassembleOperation(parser, key, annotation, configuration);
            return Collections.singletonList(operation);
        }

        // 若指定类型，则认为其为固定类型
        OperationConfiguration disassembleConfiguration;
        if (parseContext.isInLooking(disassembleType)) {
            // 存在循环依赖，则先通过缓存获取引用
            log.info("类{}与嵌套的成员变量类型{}形成循环依赖...", configuration.getTargetClass(), disassembleType);
            disassembleConfiguration = parseContext.get(disassembleType);
        } else {
            disassembleConfiguration = annotation.useCurrParser() ?
                parse(disassembleType, parseContext) : BeanFactoryUtils
                    .getBean(beanFactory, annotation.parser(), annotation.parserName())
                    .parse(disassembleType);
        }
        operation = createDisassembleOperation(key, annotation, configuration, disassembleConfiguration);
        return Collections.singletonList(operation);
    }

}
