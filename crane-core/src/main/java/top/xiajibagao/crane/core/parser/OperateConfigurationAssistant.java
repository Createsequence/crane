package top.xiajibagao.crane.core.parser;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.xiajibagao.crane.core.container.Container;
import top.xiajibagao.crane.core.helper.FuncUtils;
import top.xiajibagao.crane.core.helper.SFunc;
import top.xiajibagao.crane.core.helper.reflex.ReflexUtils;
import top.xiajibagao.crane.core.operator.interfaces.Assembler;
import top.xiajibagao.crane.core.operator.interfaces.Disassembler;
import top.xiajibagao.crane.core.parser.interfaces.AssembleProperty;
import top.xiajibagao.crane.core.parser.interfaces.DisassembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.GlobalConfiguration;
import top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

/**
 * {@link OperationConfiguration}构建辅助类 <br />
 * 允许基于{@link BeanOperationConfiguration}，或其他自定义的配置进行构建一个操作配置实例
 *
 * @author huangchengxing
 * @date 2022/04/09 21:58
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class OperateConfigurationAssistant<T> {

    @Getter
    private final OperationConfiguration configuration;

    /**
     * 基于指定实例构建操作配置
     *
     * @param configuration 操作配置
     * @return top.xiajibagao.crane.core.parser.OperateConfigurationAssistant<T>
     * @author huangchengxing
     * @date 2022/4/9 22:54
     */
    public static <T> OperateConfigurationAssistant<T> basedOn(OperationConfiguration configuration) {
        return new OperateConfigurationAssistant<>(configuration);
    }

    /**
     * 基于{@link BeanOperationConfiguration}构建操作配置实例
     *
     * @param globalConfiguration 全局配置
     * @param targetClass 目标类型
     * @author huangchengxing
     * @date 2022/4/9 22:55
     */
    public static <T> OperateConfigurationAssistant<T> basedOnBeanOperationConfiguration(
        GlobalConfiguration globalConfiguration, Class<T> targetClass) {
        return new OperateConfigurationAssistant<>(new BeanOperationConfiguration(
            globalConfiguration, targetClass, new ArrayList<>(), new ArrayList<>()
        ));
    }
    
    /**
     * 构建装配配置
     *
     * @param keyProperty key字段
     * @param container 容器
     * @return top.xiajibagao.crane.core.parser.OperateConfigurationAssistant.AssembleOperationBuilder<T>
     * @author huangchengxing
     * @date 2022/4/9 22:55
     */
    public AssembleOperationBuilder<T> buildAssembler(String keyProperty, @Nonnull Container container, @Nonnull Assembler assembler) {
        Field keyField = ReflexUtils.findField(configuration.getTargetClass(), keyProperty);
        Assert.notNull(keyField, "类[{}]中找不到属性[{}]", configuration.getTargetClass(), keyProperty);
        return new AssembleOperationBuilder<>(this, keyField, container, assembler);
    }

    /**
     * 构建装配配置
     *
     * @param keyFunc key字段对应的方法引用
     * @param container 容器
     * @return top.xiajibagao.crane.core.parser.OperateConfigurationAssistant.AssembleOperationBuilder<T>
     * @author huangchengxing
     * @date 2022/4/9 22:55
     */
    public AssembleOperationBuilder<T> buildAssembler(@Nonnull SFunc<T, ?> keyFunc, @Nonnull Container container, @Nonnull Assembler assembler) {
        return buildAssembler(FuncUtils.getPropertyName(keyFunc), container, assembler);
    }
    
    /**
     * 构建装卸配置
     *
     * @param property 待装卸的字段
     * @param targetOperateConfiguration 待装卸的字段对应的操作配置
     * @return top.xiajibagao.crane.core.parser.OperateConfigurationAssistant.DisassembleOperationBuilder<T>
     * @author huangchengxing
     * @date 2022/4/9 22:56
     */
    public DisassembleOperationBuilder<T> buildDisassembler(@Nonnull String property, @Nonnull OperationConfiguration targetOperateConfiguration, @Nonnull Disassembler assembler) {
        Field keyField = ReflexUtils.findField(configuration.getTargetClass(), property);
        Assert.notNull(keyField, "类[{}]中找不到属性[{}]", configuration.getTargetClass(), property);
        return new DisassembleOperationBuilder<>(this, keyField, targetOperateConfiguration, assembler);
    }

    /**
     * 构建装卸配置
     *
     * @param propertyFunc 待装卸的字段对应的方法引用
     * @param targetOperateConfiguration 待装卸的字段对应的操作配置
     * @return top.xiajibagao.crane.core.parser.OperateConfigurationAssistant.DisassembleOperationBuilder<T>
     * @author huangchengxing
     * @date 2022/4/9 22:56
     */
    public DisassembleOperationBuilder<T> buildDisassembler(@Nonnull SFunc<T, ?> propertyFunc, @Nonnull OperationConfiguration targetOperateConfiguration, @Nonnull Disassembler disassembler) {
        return buildDisassembler(FuncUtils.getPropertyName(propertyFunc), targetOperateConfiguration, disassembler);
    }

    @Getter
    @RequiredArgsConstructor
    public static class DisassembleOperationBuilder<T> {
        private final OperateConfigurationAssistant<T> builder;
        private final Field targetField;
        private int sort = 0;
        private final Set<String> aliases = new HashSet<>();
        private final OperationConfiguration targetOperateConfiguration;
        private final Disassembler disassembler;

        public DisassembleOperationBuilder<T> aliases(String... aliases) {
            if (ArrayUtil.isNotEmpty(aliases)) {
                this.aliases.addAll(Arrays.asList(aliases));
            }
            return this;
        }

        public DisassembleOperationBuilder<T> sort(int sort) {
            this.sort = sort;
            return this;
        }

        public OperateConfigurationAssistant<T> operation(Function<DisassembleOperationBuilder<T>, DisassembleOperation> operationFactory) {
            builder.configuration.getDisassembleOperations().add(operationFactory.apply(this));
            return builder;
        }

        public OperateConfigurationAssistant<T> build() {
            builder.configuration.getDisassembleOperations().add(new BeanDisassembleOperation(
                sort,
                builder.configuration, disassembler,
                targetOperateConfiguration, targetField, aliases
            ));
            return builder;
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class AssembleOperationBuilder<T> {
        private final OperateConfigurationAssistant<T> builder;
        private final Field targetField;
        private final Container container;
        private final Set<String> aliases = new HashSet<>();
        private final List<AssembleProperty> properties = new ArrayList<>();
        private final Assembler assembler;
        private String namespace = "";
        private int order = 0;

        public AssembleOperationBuilder<T> aliases(String... aliases) {
            if (ArrayUtil.isNotEmpty(aliases)) {
                this.aliases.addAll(Arrays.asList(aliases));
            }
            return this;
        }

        public AssembleOperationBuilder<T> sort(int order) {
            this.order = order;
            return this;
        }

        public AssembleOperationBuilder<T> namespace(String namespace) {
            this.namespace = CharSequenceUtil.blankToDefault(namespace, "");
            return this;
        }

        public AssembleOperationBuilder<T> properties(Collection<AssembleProperty> properties) {
            if (CollUtil.isNotEmpty(properties)) {
                this.properties.addAll(properties);
            }
            return this;
        }

        public AssembleOperationBuilder<T> property(String resource, String reference) {
            return property(resource, reference, "", Void.class);
        }

        public AssembleOperationBuilder<T> property(String resource, String reference, String exp, Class<?> expType) {
            properties.add(new BeanAssembleProperty(
                CharSequenceUtil.blankToDefault(reference, ""),
                CharSequenceUtil.blankToDefault(resource, ""),
                exp, expType
            ));
            return this;
        }

        public AssembleOperationBuilder<T> property(String resource, SFunc<T, ?> reference) {
            return property(resource, FuncUtils.getPropertyName(reference));
        }

        public <S> AssembleOperationBuilder<T> property(SFunc<S, ?> resource, SFunc<T, ?> reference) {
            return property(FuncUtils.getPropertyName(resource), FuncUtils.getPropertyName(reference));
        }

        public AssembleOperationBuilder<T> onlyRefProperty(SFunc<T, ?> reference) {
            return property("", FuncUtils.getPropertyName(reference));
        }

        public AssembleOperationBuilder<T> onlyRefProperty(String reference) {
            return property("", reference);
        }

        public <S> AssembleOperationBuilder<T> onlySrcProperty(SFunc<S, ?> resource) {
            return property(FuncUtils.getPropertyName(resource), "");
        }

        public AssembleOperationBuilder<T> onlySrcProperty(String resource) {
            return property(resource, "");
        }

        public OperateConfigurationAssistant<T> build() {
            builder.configuration.getAssembleOperations().add(new BeanAssembleOperation(
                order,
                builder.configuration,
                targetField, aliases,
                namespace, container,
                assembler, properties
            ));
            return builder;
        }

    }

}
