package top.xiajibagao.crane.starter;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import top.xiajibagao.crane.core.annotation.MethodSourceBean;
import top.xiajibagao.crane.core.aop.MethodResultProcessAspect;
import top.xiajibagao.crane.core.cache.ConfigurationCache;
import top.xiajibagao.crane.core.cache.OperationConfigurationCache;
import top.xiajibagao.crane.core.container.EnumDictContainer;
import top.xiajibagao.crane.core.container.IntrospectContainer;
import top.xiajibagao.crane.core.container.KeyValueContainer;
import top.xiajibagao.crane.core.container.MethodSourceContainer;
import top.xiajibagao.crane.core.executor.OperationExecutor;
import top.xiajibagao.crane.core.executor.SequentialOperationExecutor;
import top.xiajibagao.crane.core.executor.UnorderedOperationExecutor;
import top.xiajibagao.crane.core.handler.*;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandlerChain;
import top.xiajibagao.crane.core.helper.EnumDict;
import top.xiajibagao.crane.core.helper.OperateTemplate;
import top.xiajibagao.crane.core.operator.BeanReflexAssembler;
import top.xiajibagao.crane.core.operator.BeanReflexDisassembler;
import top.xiajibagao.crane.core.parser.BeanOperateConfigurationParser;
import top.xiajibagao.crane.core.parser.interfaces.GlobalConfiguration;
import top.xiajibagao.crane.core.parser.interfaces.OperateConfigurationParser;
import top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration;
import top.xiajibagao.crane.jackson.impl.handler.ArrayNodeOperateHandler;
import top.xiajibagao.crane.jackson.impl.handler.ObjectNodeOperateHandler;
import top.xiajibagao.crane.jackson.impl.handler.ValueNodeOperateHandler;
import top.xiajibagao.crane.jackson.impl.module.DynamicJsonNodeModule;
import top.xiajibagao.crane.jackson.impl.operator.JacksonAssembler;
import top.xiajibagao.crane.jackson.impl.operator.JacksonDisassembler;

import java.util.Map;

/**
 * 默认配置
 *
 * @author huangchengxing
 * @date 2022/03/03 13:36
 */
@Configuration
public class CraneAutoConfiguration {

    /**
     * 基础配置
     */
    @AutoConfigureAfter(CraneConfigurationProperties.class)
    @Configuration
    public static class DefaultCraneAutoConfiguration {

        // ==================== 解析器 ====================

        @Order
        @ConditionalOnMissingBean(BeanOperateConfigurationParser.class)
        @Bean("DefaultCraneBeanOperateConfigurationParser")
        public BeanOperateConfigurationParser beanOperateConfigurationParser(GlobalConfiguration configuration, BeanFactory beanFactory) {
            return new BeanOperateConfigurationParser(configuration, beanFactory);
        }

        // ==================== 操作者 ====================

        @Order
        @ConditionalOnMissingBean(OrderlyOperateHandlerChain.class)
        @Bean("DefaultCraneOrderlyOperateHandlerChain")
        public OperateHandlerChain orderlyOperateHandlerChain() {
            OrderlyOperateHandlerChain operateHandlerChain = new OrderlyOperateHandlerChain();
            operateHandlerChain.addHandler(new MapOperateHandler())
                .addHandler(new CollectionOperateHandler(operateHandlerChain))
                .addHandler(new ArrayOperateHandler(operateHandlerChain))
                .addHandler(new MapOperateHandler())
                .addHandler(new BeanOperateHandler());
            return new ExpressibleOperateHandlerChain(operateHandlerChain, StandardEvaluationContext::new);
        }

        @Order
        @ConditionalOnMissingBean(BeanReflexAssembler.class)
        @Bean("DefaultCraneBeanReflexAssembler")
        public BeanReflexAssembler beanReflexAssembler(@Qualifier("DefaultCraneOrderlyOperateHandlerChain") OperateHandlerChain assembleHandlerChain) {
            return new BeanReflexAssembler(assembleHandlerChain);
        }

        @Order
        @ConditionalOnMissingBean(BeanReflexDisassembler.class)
        @Bean("DefaultCraneBeanReflexDisassembler")
        public BeanReflexDisassembler beanReflexDisassembler(@Qualifier("DefaultCraneOrderlyOperateHandlerChain") OperateHandlerChain assembleHandlerChain) {
            return new BeanReflexDisassembler(assembleHandlerChain);
        }

        // ==================== 容器 ====================

        @Order
        @ConditionalOnMissingBean(EnumDict.class)
        @Bean("DefaultCraneEnumDict")
        public EnumDict enumDict() {
            return EnumDict.instance();
        }

        @Order
        @ConditionalOnMissingBean(EnumDictContainer.class)
        @Bean("DefaultCraneEnumDictContainer")
        public EnumDictContainer enumDictContainer(EnumDict enumDict) {
            return new EnumDictContainer(enumDict);
        }

        @Order
        @ConditionalOnMissingBean(KeyValueContainer.class)
        @Bean("DefaultCraneKeyValueContainer")
        public KeyValueContainer simpleKeyValueActuator() {
            return new KeyValueContainer();
        }

        // ==================== 执行器 ====================

        @Order
        @ConditionalOnMissingBean(UnorderedOperationExecutor.class)
        @Bean("DefaultCraneUnorderedOperationExecutor")
        public UnorderedOperationExecutor unorderedOperationExecutor() {
            return new UnorderedOperationExecutor();
        }

        @Order
        @ConditionalOnMissingBean(SequentialOperationExecutor.class)
        @Bean("DefaultCraneSequentialOperationExecutor")
        public SequentialOperationExecutor operationExecutor() {
            return new SequentialOperationExecutor();
        }

    }

    @AutoConfigureAfter(DefaultCraneAutoConfiguration.class)
    @Configuration
    public static class DefaultCraneExtensionAutoConfiguration {

        @Order
        @ConditionalOnMissingBean(MethodSourceContainer.class)
        @Bean("DefaultCraneMethodSourceContainer")
        public MethodSourceContainer methodSourceContainer(ApplicationContext applicationContext) {
            MethodSourceContainer container = new MethodSourceContainer();
            Map<String, Object> beans = applicationContext.getBeansWithAnnotation(MethodSourceBean.class);
            if (CollUtil.isNotEmpty(beans)) {
                beans.forEach((name, bean) -> container.register(bean));
            }
            return container;
        }

        @Order
        @ConditionalOnMissingBean(MethodSourceContainer.class)
        @Bean("DefaultCraneIntrospectContainer")
        public IntrospectContainer introspectContainer() {
            return new IntrospectContainer();
        }

        @Order
        @ConditionalOnMissingBean(OperationConfigurationCache.class)
        @Bean("DefaultCraneOperationConfigurationCache")
        public OperationConfigurationCache operationConfigurationCache() {
            return new OperationConfigurationCache();
        }

        @Order
        @ConditionalOnMissingBean(MethodResultProcessAspect.class)
        @Bean("DefaultCraneMethodResultProcessAspect")
        public MethodResultProcessAspect methodResultProcessAspect(BeanFactory beanFactory, @Qualifier("DefaultCraneOperationConfigurationCache") ConfigurationCache configurationCache) {
            return new MethodResultProcessAspect(beanFactory, configurationCache);
        }

        @Order
        @ConditionalOnMissingBean(OperateTemplate.class)
        @Bean("DefaultCraneOperateHelper")
        public OperateTemplate operateHelper(
            @Qualifier("DefaultCraneOperationConfigurationCache") ConfigurationCache configurationCache,
            @Qualifier("DefaultCraneBeanOperateConfigurationParser") OperateConfigurationParser<? extends OperationConfiguration> defaultOperateConfigurationParser,
            @Qualifier("DefaultCraneUnorderedOperationExecutor") OperationExecutor defaultOperationExecutor) {
            return new OperateTemplate(configurationCache, defaultOperateConfigurationParser, defaultOperationExecutor);
        }

    }

    @AutoConfigureAfter(DefaultCraneAutoConfiguration.class)
    @ConditionalOnClass({JacksonAssembler.class, JacksonDisassembler.class})
    @Configuration
    public static class DefaultCraneJacksonAutoConfiguration {

        @Order
        @Bean("DefaultCraneJacksonObjectMapper")
        public ObjectMapper objectMapper() {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return objectMapper;
        }

        @Order
        @ConditionalOnMissingBean(OrderlyOperateHandlerChain.class)
        @Bean("DefaultCraneJacksonOrderlyOperateHandlerChain")
        public OperateHandlerChain orderlyOperateHandlerChain(@Qualifier("DefaultCraneJacksonObjectMapper") ObjectMapper objectMapper) {
            OrderlyOperateHandlerChain assembleHandlerChain = new OrderlyOperateHandlerChain();
            assembleHandlerChain.addHandler(new ArrayNodeOperateHandler(objectMapper, assembleHandlerChain))
                .addHandler(new ObjectNodeOperateHandler(objectMapper))
                .addHandler(new ValueNodeOperateHandler(objectMapper));
            return new ExpressibleOperateHandlerChain(assembleHandlerChain, StandardEvaluationContext::new);
        }

        @Order
        @ConditionalOnMissingBean(JacksonAssembler.class)
        @Bean("DefaultCraneJacksonAssembler")
        public JacksonAssembler jacksonAssembler(
            @Qualifier("DefaultCraneJacksonObjectMapper") ObjectMapper objectMapper,
            @Qualifier("DefaultCraneJacksonOrderlyOperateHandlerChain") OperateHandlerChain assembleHandlerChain) {
            return new JacksonAssembler(objectMapper, assembleHandlerChain);
        }

        @Order
        @ConditionalOnMissingBean(JacksonDisassembler.class)
        @Bean("DefaultCraneJacksonDisassembler")
        public JacksonDisassembler jacksonDisassembler(@Qualifier("DefaultCraneJacksonObjectMapper") ObjectMapper objectMapper) {
            return new JacksonDisassembler(objectMapper);
        }

        @Order
        @Bean("DefaultCraneJacksonDynamicJsonNodeModule")
        public DynamicJsonNodeModule dynamicJsonNodeModule(
            BeanFactory beanFactory,
            @Qualifier("DefaultCraneJacksonObjectMapper") ObjectMapper defaultObjectMapper) {
            return new DynamicJsonNodeModule(beanFactory, defaultObjectMapper);
        }

        @Order
        @Bean("DefaultCraneJacksonSerializeObjectMapper")
        public ObjectMapper serializeObjectMapper(@Qualifier("DefaultCraneJacksonDynamicJsonNodeModule")DynamicJsonNodeModule dynamicJsonNodeModule) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(dynamicJsonNodeModule);
            return objectMapper;
        }

    }

    /**
     * @author huangchengxing
     * @date 2022/05/05 23:49
     */
    @Configuration
    @ConfigurationProperties(prefix = "crane")
    @Accessors(fluent = true)
    @Data
    public static class CraneConfigurationProperties implements GlobalConfiguration {
    }
}
