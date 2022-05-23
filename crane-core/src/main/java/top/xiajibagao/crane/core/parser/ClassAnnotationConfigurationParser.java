package top.xiajibagao.crane.core.parser;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.google.common.base.Predicates;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import top.xiajibagao.crane.core.annotation.Assemble;
import top.xiajibagao.crane.core.annotation.Disassemble;
import top.xiajibagao.crane.core.annotation.Operations;
import top.xiajibagao.crane.core.helper.CollUtils;
import top.xiajibagao.crane.core.helper.Orderly;
import top.xiajibagao.crane.core.helper.reflex.ReflexUtils;
import top.xiajibagao.crane.core.parser.interfaces.*;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 解析类{@link Operations}注解，生成配置类
 *
 * @author huangchengxing
 * @date 2022/05/22 14:52
 */
@Slf4j
public class ClassAnnotationConfigurationParser
    extends AbstractAnnotationConfigurationParser
    implements OperateConfigurationParser {

    public ClassAnnotationConfigurationParser(GlobalConfiguration globalConfiguration, BeanFactory beanFactory) {
        super(globalConfiguration, beanFactory);
    }

    @Nonnull
    @Override
    protected BeanOperationConfiguration parse(Class<?> targetClass, ParseContext parseContext) {
        BeanOperationConfiguration configuration = createConfiguration(targetClass);
        List<AssembleOperation> assembleOperations = new ArrayList<>();
        List<DisassembleOperation> disassembleOperations = new ArrayList<>();

        // 优先解析父节点
        Operations rootAnnotation = parseAnnotation(parseContext, configuration, assembleOperations, disassembleOperations);
        if (Objects.isNull(rootAnnotation)) {
            return configuration;
        }

        // 若允许继承父类及接口的注解的配置，则一并解析
        if (rootAnnotation.enableExtend()) {
            ReflexUtils.forEachClass(
                targetClass, Predicates.not(parseContext::isExcluded),
                t -> parseAnnotation(parseContext, configuration, assembleOperations, disassembleOperations)
            );
        }
        // 排序
        configuration.getAssembleOperations().addAll(CollUtil.sort(assembleOperations, Orderly::compareTo));
        configuration.getDisassembleOperations().addAll(CollUtil.sort(disassembleOperations, Orderly::compareTo));
        return configuration;
    }

    /**
     * 若类上存在{@link Operations}注解，则将注解解析为对应的操作配置，并填入对应的收集中的操作配置集合
     *
     * @param parseContext 解析上下文
     * @param configuration 当前正在构建的配置
     * @param assembleOperations 正在收集的装配操作
     * @param disassembleOperations 正在收集的装卸操作
     * @return top.xiajibagao.crane.core.annotation.Operations
     * @author huangchengxing
     * @date 2022/5/22 16:44
     */
    private Operations parseAnnotation(ParseContext parseContext, BeanOperationConfiguration configuration, List<AssembleOperation> assembleOperations, List<DisassembleOperation> disassembleOperations) {
        Class<?> targetClass = configuration.getTargetClass();
        Operations annotation = AnnotatedElementUtils.findMergedAnnotation(targetClass, Operations.class);
        if (Objects.nonNull(annotation)) {
            assembleOperations.addAll(parseAssembleAnnotations(configuration, annotation));
            disassembleOperations.addAll(parseDisassembleAnnotations(configuration, annotation, parseContext));
            // 在上下文中表明当前配置类正在构建
            parseContext.looking(targetClass, configuration);
            parseContext.exclude(annotation.extendExcludes());
        }
        return annotation;
    }

    /**
     * 创建一个配置
     *
     * @param targetClass 目标类型
     * @return T
     * @author huangchengxing
     * @date 2022/3/1 16:24
     */
    @Nonnull
    protected BeanOperationConfiguration createConfiguration(Class<?> targetClass) {
        return new BeanOperationConfiguration(globalConfiguration, targetClass, new ArrayList<>(), new ArrayList<>());
    }

    // ====================== 解析装配解 ======================

    /**
     * 解析{@link Operations#assembles()}注解
     *
     * @param configuration 当前正在构建的配置类
     * @param operations 注解
     * @return java.util.List<top.xiajibagao.crane.core.parser.interfaces.AssembleOperation>
     * @author huangchengxing
     * @date 2022/5/22 16:33
     */
    protected List<AssembleOperation> parseAssembleAnnotations(OperationConfiguration configuration, Operations operations) {
        Multimap<String, Assemble> assembleKeys = ArrayListMultimap.create();
        for (Assemble annotation : operations.assembles()) {
            assembleKeys.put(annotation.key(), annotation);
        }
        List<AssembleOperation> results = new ArrayList<>();
        Class<?> targetClass = configuration.getTargetClass();
        assembleKeys.asMap().forEach((keyName, annotations) -> {
            // 先根据key寻找属性，若不存在则根据key别名寻找
            Field key = ReflexUtils.findField(targetClass, keyName, false);
            boolean keyExists = Objects.nonNull(key);
            annotations.stream()
                .map(a -> createAssembleOperation(keyExists ? key : ReflexUtils.findAnyMatchField(targetClass, true, a.aliases()), a, configuration))
                .forEach(results::add);
        });
        return results;
    }

    // ====================== 解析装卸注解 =====================
    
    /**
     * 解析{@link Operations#disassembles()}注解
     *
     * @param configuration 当前正在构建的操作配置
     * @param operations 注解
     * @param parseContext 上下文
     * @return java.util.List<top.xiajibagao.crane.core.parser.interfaces.DisassembleOperation>
     * @author huangchengxing
     * @date 2022/5/22 16:48
     */
    protected List<DisassembleOperation> parseDisassembleAnnotations(OperationConfiguration configuration, Operations operations, ParseContext parseContext) {
        // 按key分组，当一个key字段存在重复的拆卸配置时，总是保留最早解析的配置
        Map<String, Disassemble> disassembleKeys = CollUtils.toList(operations.disassembles()).stream()
            .collect(Collectors.toMap(Disassemble::key, Function.identity(), (k1, k2) -> k1));
        List<DisassembleOperation> results = new ArrayList<>();
        disassembleKeys.forEach((keyName, annotation) -> {
            Field key = ReflexUtils.findAnyMatchField(configuration.getTargetClass(), true, ArrayUtil.insert(annotation.aliases(), 0, annotation.key()));
            Class<?> disassembleType = annotation.targetClass();
            // 若存在循环依赖，则直接从上下文获取配置的引用，否则递归解析待装卸的字段类型
            OperationConfiguration disassembleConfiguration = parseContext.isInLooking(disassembleType) ?
                parseContext.get(disassembleType) : parse(disassembleType, parseContext);
            DisassembleOperation disassembleOperation = createDisassembleOperation(
                key, annotation, configuration, disassembleConfiguration
            );
            results.add(disassembleOperation);
        });
        return results;
    }

}
