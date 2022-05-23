package top.xiajibagao.crane.core.parser;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.CollectionUtils;
import top.xiajibagao.crane.core.annotation.Assemble;
import top.xiajibagao.crane.core.annotation.Disassemble;
import top.xiajibagao.crane.core.exception.CraneException;
import top.xiajibagao.crane.core.helper.ObjectUtils;
import top.xiajibagao.crane.core.helper.Orderly;
import top.xiajibagao.crane.core.helper.reflex.ReflexUtils;
import top.xiajibagao.crane.core.parser.interfaces.*;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 解析类属性中的{@link Assemble}与{@link Disassemble}注解，生成配置类
 *
 * @author huangchengxing
 * @date 2022/03/01 16:13
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
    protected BeanOperationConfiguration parse(Class<?> targetClass, ParseContext parseContext) {
        BeanOperationConfiguration operationConfiguration = createConfiguration(targetClass);
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
    @Nonnull
    protected BeanOperationConfiguration createConfiguration(Class<?> targetClass) {
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
    protected List<AssembleOperation> parseAssembleAnnotationOnField(Field key, BeanOperationConfiguration configuration) {
        Assemble assemble = AnnotatedElementUtils.getMergedAnnotation(key, Assemble.class);
        List<AssembleOperation> operations = ObjectUtils.computeIfNotNull(
            AnnotatedElementUtils.getMergedAnnotation(key, Assemble.List.class),
            list -> CollStreamUtil.toList(
                Arrays.asList(list.value()), a -> createAssembleOperation(key, a, configuration)
            ), new ArrayList<>()
        );
        if (Objects.nonNull(assemble)) {
            operations.add(createAssembleOperation(key, assemble, configuration));
        }

        CraneException.throwIfFalse(
            CollectionUtils.isEmpty(operations) || !AnnotatedElementUtils.hasAnnotation(key, Disassemble.class),
            "属性[%s]无法同时被%s和%s注解标记",
            configuration.getTargetClass(), key, Assemble.class, Disassemble.class
        );
        return operations;
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
    protected List<DisassembleOperation> parseDisassembleAnnotationOnField(Field key, BeanOperationConfiguration configuration, ParseContext parseContext) {
        Disassemble disassemble = AnnotatedElementUtils.findMergedAnnotation(key, Disassemble.class);
        if (Objects.isNull(disassemble)) {
            return Collections.emptyList();
        }
        CraneException.throwIfTrue(
            AnnotatedElementUtils.hasAnnotation(key, Assemble.class),
            "属性[%s]无法同时被%s和%s注解标记", configuration.getTargetClass(), key, Assemble.class, Disassemble.class
        );

        // 递归解析拆卸字段类型
        parseContext.looking(configuration.getTargetClass(), configuration);
        Class<?> operateClass = disassemble.value();
        OperationConfiguration operationConfiguration;
        if (parseContext.isInLooking(operateClass)) {
            // 存在循环依赖，则先通过缓存获取引用
            log.info("类{}与嵌套的成员变量类型{}形成循环依赖...", configuration.getTargetClass(), operateClass);
            operationConfiguration = parseContext.get(operateClass);
        } else {
            operationConfiguration = parse(operateClass, parseContext);
        }
        DisassembleOperation operation = createDisassembleOperation(key, disassemble, configuration, operationConfiguration);

        return Collections.singletonList(operation);
    }

}
