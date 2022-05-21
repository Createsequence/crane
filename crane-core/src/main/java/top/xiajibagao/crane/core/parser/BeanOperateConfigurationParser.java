package top.xiajibagao.crane.core.parser;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import top.xiajibagao.crane.core.annotation.*;
import top.xiajibagao.crane.core.exception.CraneException;
import top.xiajibagao.crane.core.helper.BeanFactoryUtils;
import top.xiajibagao.crane.core.helper.CollUtils;
import top.xiajibagao.crane.core.helper.ObjectUtils;
import top.xiajibagao.crane.core.helper.Orderly;
import top.xiajibagao.crane.core.helper.reflex.ReflexUtils;
import top.xiajibagao.crane.core.parser.interfaces.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 类型配置解析器
 *
 * @author huangchengxing
 * @date 2022/03/01 16:13
 */
@Slf4j
@RequiredArgsConstructor
public class BeanOperateConfigurationParser implements OperateConfigurationParser<BeanOperationConfiguration> {

    private final GlobalConfiguration configuration;
    private final BeanFactory beanFactory;

    @Override
    public BeanOperationConfiguration parse(Class<?> targetClass) {
        return parse(targetClass, new FieldAnnotationParseContext());
    }

    private BeanOperationConfiguration parse(Class<?> targetClass, FieldAnnotationParseContext parseContext) {
        BeanOperationConfiguration operationConfiguration = createConfiguration(targetClass);
        List<AssembleOperation> assembleOperations = CollUtil.newArrayList(parseAssembleAnnotationClass(targetClass, operationConfiguration));
        List<DisassembleOperation> disassembleOperations = new ArrayList<>();
        // 解析属性注解获取操作配置
        ReflexUtils.forEachFromClass(targetClass, Class::getDeclaredFields, f -> {
            assembleOperations.addAll(parseAssembleAnnotationOnField(f, operationConfiguration));
            disassembleOperations.addAll(parseDisassembleAnnotationOnField(f, operationConfiguration, parseContext));
        });
        // 按sort排序
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
    protected BeanOperationConfiguration createConfiguration(Class<?> targetClass) {
        return new BeanOperationConfiguration(configuration, targetClass, new ArrayList<>(), new ArrayList<>());
    }

    // =========================== 解析类上的装配注解 ===========================

    /**
     * 解析注解{@link Operations#assembles()}
     *
     * @param targetClass 目标类
     * @param configuration 配置
     * @author huangchengxing
     * @date 2022/5/20 13:56
     */
    protected Collection<AssembleOperation> parseAssembleAnnotationClass(Class<?> targetClass, BeanOperationConfiguration configuration) {
        ClassAnnotationParseContext context = new ClassAnnotationParseContext();
        parseAssembleAnnotationOnClass(targetClass, configuration, context);
        return context.getFoundOperation().values();
    }

    /**
     * 解析注解{@link Operations#assembles()}
     *
     * @param targetClass 目标类
     * @param configuration 配置
     * @param parseContext 解析上下文
     * @author huangchengxing
     * @date 2022/5/20 13:56
     */
    protected void parseAssembleAnnotationOnClass(
        Class<?> targetClass, BeanOperationConfiguration configuration, ClassAnnotationParseContext parseContext) {
        if (parseContext.isExcluded(targetClass) || parseContext.isFound(targetClass)) {
            return;
        }

        // 解析本类上注解
        Operations annotation = AnnotatedElementUtils.findMergedAnnotation(targetClass, Operations.class);
        if (Objects.isNull(annotation)) {
            return;
        }
        List<AssembleOperation> parsedOperations = new ArrayList<>();
        for (Assemble assemble : annotation.assembles()) {
            Field property = ReflexUtils.findField(targetClass, assemble.key(), true);
            AssembleOperation operation = createAssembleAnnotation(property, assemble, configuration);
            parsedOperations.add(operation);
        }
        parseContext.found(targetClass, parsedOperations);
        parseContext.exclude(annotation.extendExcludes());

        // 解析扩展类
        if (ArrayUtil.isEmpty(annotation.extendFrom())) {
            return;
        }
        for (Class<?> extendClass : annotation.extendFrom()) {
            parseAssembleAnnotationOnClass(extendClass, configuration, parseContext);
        }
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
    protected List<AssembleOperation> parseAssembleAnnotationOnField(Field key, BeanOperationConfiguration configuration) {
        Assemble assemble = AnnotatedElementUtils.getMergedAnnotation(key, Assemble.class);
        List<AssembleOperation> operations = ObjectUtils.computeIfNotNull(
            AnnotatedElementUtils.getMergedAnnotation(key, Assemble.List.class),
            list -> CollStreamUtil.toList(
                Arrays.asList(list.value()), a -> createAssembleAnnotation(key, a, configuration)
            ), new ArrayList<>()
        );
        if (Objects.nonNull(assemble)) {
            operations.add(createAssembleAnnotation(key, assemble, configuration));
        }

        CraneException.throwIfFalse(
            CollectionUtils.isEmpty(operations) || !AnnotatedElementUtils.hasAnnotation(key, Disassemble.class),
            "[{}]属性[{}]无法同时被{}和{}注解标记",
            configuration.getTargetClass(), key, Assemble.class, Disassemble.class
        );
        return operations;
    }
    
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
    protected List<DisassembleOperation> parseDisassembleAnnotationOnField(Field key, BeanOperationConfiguration configuration, FieldAnnotationParseContext parseContext) {
        Disassemble disassemble = AnnotatedElementUtils.findMergedAnnotation(key, Disassemble.class);
        if (Objects.isNull(disassemble)) {
            return Collections.emptyList();
        }
        CraneException.throwIfTrue(
            AnnotatedElementUtils.hasAnnotation(key, Assemble.class),
            "[%s]属性[$s]无法同时被%s和%s注解标记", configuration.getTargetClass(), key, Assemble.class, Disassemble.class
        );

        // 递归解析拆卸字段类型
        parseContext.lookingFor(configuration);
        Class<?> operateClass = disassemble.value();
        OperationConfiguration operationConfiguration;
        if (parseContext.isInLooking(operateClass)) {
            // 存在循环依赖，则先通过缓存获取引用
            log.info(CharSequenceUtil.format("类{}与嵌套的成员变量类型{}形成循环依赖...", configuration.getTargetClass(), operateClass));
            operationConfiguration = parseContext.getLookingForConfig(operateClass);
        } else {
            operationConfiguration = parse(operateClass, parseContext);
        }
        DisassembleOperation operation = createDisassembleOperation(key, disassemble, configuration, operationConfiguration, parseContext);
        parseContext.found(configuration);

        return Collections.singletonList(operation);
    }
    
    /**
     * 根据{@link Disassemble}注解创建{@link DisassembleOperation}
     *
     * @param key 属性
     * @param annotation {@link Disassemble}注解
     * @param configuration 当前正在构建的配置
     * @param operationConfiguration 待拆卸属性的类型配置
     * @param parseContext 解析上下文，用于缓存配置处理循环依赖
     * @return DisassembleOperation
     * @author huangchengxing
     * @date 2022/3/1 17:50
     */
    protected DisassembleOperation createDisassembleOperation(
		    Field key, Disassemble annotation, BeanOperationConfiguration configuration, OperationConfiguration operationConfiguration, FieldAnnotationParseContext parseContext) {
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

    protected static class ClassAnnotationParseContext {

        @Getter
        private final Multimap<Class<?>, AssembleOperation> foundOperation = ArrayListMultimap.create();

        private final Set<Class<?>> excluded = new HashSet<>();

        public void found(Class<?> targetClass, Collection<AssembleOperation> operations) {
            foundOperation.putAll(targetClass, operations);
        }

        public boolean isFound(Class<?> targetClass) {
            return foundOperation.containsKey(targetClass);
        }

        public void exclude(Class<?>... excludeClass) {
            excluded.addAll(Arrays.asList(excludeClass));
        }

        public boolean isExcluded(Class<?> excludeClass) {
            return excluded.contains(excludeClass);
        }

    }

    protected static class FieldAnnotationParseContext {

        private final Map<Class<?>, OperationConfiguration> inLookingOption = new HashMap<>();

        public boolean isInLooking(Class<?> target) {
            return inLookingOption.containsKey(target);
        }

        public void lookingFor(OperationConfiguration target) {
            inLookingOption.put(target.getTargetClass(), target);
        }

        public OperationConfiguration getLookingForConfig(Class<?> target) {
            OperationConfiguration config = inLookingOption.get(target);
            Assert.notNull(config, String.format("类[%s]不处于解析状态，或已经完成解析", target));
            return config;
        }

        public void found(OperationConfiguration config) {
            Class<?> foundClass = config.getTargetClass();
            Assert.notNull(config, String.format("类[%s]不处于解析状态，或已经完成解析", foundClass));
        }

    }

}
