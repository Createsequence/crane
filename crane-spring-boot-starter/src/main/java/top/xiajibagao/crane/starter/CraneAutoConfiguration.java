package top.xiajibagao.crane.starter;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import top.xiajibagao.annotation.MethodSourceBean;
import top.xiajibagao.crane.core.container.EnumDictContainer;
import top.xiajibagao.crane.core.container.KeyValueContainer;
import top.xiajibagao.crane.core.executor.OperationExecutor;
import top.xiajibagao.crane.core.executor.SequentialOperationExecutor;
import top.xiajibagao.crane.core.executor.UnorderedOperationExecutor;
import top.xiajibagao.crane.core.handler.*;
import top.xiajibagao.crane.core.helper.EnumDict;
import top.xiajibagao.crane.core.operator.BeanReflexOperatorFactory;
import top.xiajibagao.crane.core.operator.interfaces.OperatorFactory;
import top.xiajibagao.crane.core.parser.BeanGlobalConfiguration;
import top.xiajibagao.crane.core.parser.BeanOperateConfigurationParser;
import top.xiajibagao.crane.core.parser.interfaces.GlobalConfiguration;
import top.xiajibagao.crane.core.parser.interfaces.OperateConfigurationParser;
import top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration;
import top.xiajibagao.crane.extension.aop.MethodResultProcessAspect;
import top.xiajibagao.crane.extension.cache.ConfigurationCache;
import top.xiajibagao.crane.extension.cache.OperationConfigurationCache;
import top.xiajibagao.crane.extension.container.MethodSourceContainer;
import top.xiajibagao.crane.extension.helper.OperateHelper;
import top.xiajibagao.crane.jackson.impl.handler.ArrayNodeAssembleHandler;
import top.xiajibagao.crane.jackson.impl.handler.ObjectNodeAssembleHandler;
import top.xiajibagao.crane.jackson.impl.handler.ValueNodeAssembleHandler;
import top.xiajibagao.crane.jackson.impl.module.DynamicJsonNodeModule;
import top.xiajibagao.crane.jackson.impl.operator.JacksonOperatorFactory;

import java.util.Map;

/**
 * 默认配置
 *
 * @author huangchengxing
 * @date 2022/03/03 13:36
 */
public class CraneAutoConfiguration {

    @Order
    @ConditionalOnMissingBean(GlobalConfiguration.class)
    @Bean("DefaultCraneGlobalConfiguration")
    public GlobalConfiguration globalConfiguration() {
        return new BeanGlobalConfiguration();
    }
    
    // ==================== 解析器 ====================

    @Order
    @ConditionalOnMissingBean(BeanOperateConfigurationParser.class)
    @Bean("DefaultCraneBeanOperateConfigurationParser")
    public BeanOperateConfigurationParser beanOperateConfigurationParser(GlobalConfiguration configuration, BeanFactory beanFactory) {
        return new BeanOperateConfigurationParser(configuration, beanFactory);
    }

    // ==================== 操作者 ====================

    @Order
    @ConditionalOnMissingBean(OrderlyAssembleHandlerChain.class)
    @Bean("DefaultCraneOrderlyAssembleHandlerChain")
    public OrderlyAssembleHandlerChain orderlyAssembleHandlerChain() {
        OrderlyAssembleHandlerChain assembleHandlerChain = new OrderlyAssembleHandlerChain();
        assembleHandlerChain.addHandler(new MapAssembleHandler())
            .addHandler(new CollectionAssembleHandler(assembleHandlerChain))
            .addHandler(new ArrayAssembleHandler(assembleHandlerChain))
            .addHandler(new MapAssembleHandler())
            .addHandler(new BeanAssembleHandler());
        return assembleHandlerChain;
    }

    @Order
    @ConditionalOnMissingBean(BeanReflexOperatorFactory.class)
    @Bean("DefaultCraneBeanReflexOperatorFactory")
    public BeanReflexOperatorFactory reflexOperatorFactory(AssembleHandlerChain assembleHandlerChain) {
        return new BeanReflexOperatorFactory(assembleHandlerChain);
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
    
    // ==================== 扩展 ====================

    @Order
    @ConditionalOnMissingBean(OperationConfigurationCache.class)
    @Bean("DefaultCraneOperationConfigurationCache")
    public OperationConfigurationCache operationConfigurationCache() {
        return new OperationConfigurationCache();
    }
    
    @Order
    @ConditionalOnMissingBean(MethodResultProcessAspect.class)
    @Bean("DefaultCraneMethodResultProcessAspect")
    public MethodResultProcessAspect methodResultProcessAspect(BeanFactory beanFactory, ConfigurationCache configurationCache) {
        return new MethodResultProcessAspect(beanFactory, configurationCache);
    }

    @Order
    @ConditionalOnMissingBean(OperateHelper.class)
    @Bean("DefaultCraneOperateHelper")
    public OperateHelper operateHelper(
        @Qualifier("DefaultCraneOperationConfigurationCache") ConfigurationCache configurationCache,
        @Qualifier("DefaultCraneBeanReflexOperatorFactory") OperatorFactory defaultOperatorFactory,
        @Qualifier("DefaultCraneBeanOperateConfigurationParser") OperateConfigurationParser<? extends OperationConfiguration> defaultOperateConfigurationParser,
        @Qualifier("DefaultCraneUnorderedOperationExecutor") OperationExecutor defaultOperationExecutor) {
        return new OperateHelper(configurationCache, defaultOperatorFactory, defaultOperateConfigurationParser, defaultOperationExecutor);
    }

    @AutoConfigureAfter(CraneAutoConfiguration.class)
    @ConditionalOnClass(JacksonOperatorFactory.class)
    @Configuration
    public static class CraneJacksonAutoConfiguration {

        @Order
        @Bean("DefaultCraneJacksonObjectMapper")
        public ObjectMapper objectMapper() {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return objectMapper;
        }

        @Order
        @ConditionalOnMissingBean(OrderlyAssembleHandlerChain.class)
        @Bean("DefaultCraneJacksonOrderlyAssembleHandlerChain")
        public OrderlyAssembleHandlerChain orderlyAssembleHandlerChain(@Qualifier("DefaultCraneJacksonObjectMapper") ObjectMapper objectMapper) {
            OrderlyAssembleHandlerChain assembleHandlerChain = new OrderlyAssembleHandlerChain();
            assembleHandlerChain.addHandler(new ArrayNodeAssembleHandler(objectMapper, assembleHandlerChain))
                .addHandler(new ObjectNodeAssembleHandler(objectMapper))
                .addHandler(new ValueNodeAssembleHandler(objectMapper));
            return assembleHandlerChain;
        }

        @Order
        @ConditionalOnMissingBean(BeanReflexOperatorFactory.class)
        @Bean("DefaultCraneJacksonOperatorFactory")
        public JacksonOperatorFactory jacksonOperatorFactory(
            @Qualifier("DefaultCraneJacksonObjectMapper") ObjectMapper objectMapper,
            @Qualifier("DefaultCraneJacksonOrderlyAssembleHandlerChain") AssembleHandlerChain assembleHandlerChain) {
            return new JacksonOperatorFactory(objectMapper, assembleHandlerChain);
        }

        @Order
        @Bean("DefaultCraneJacksonDynamicJsonNodeModule")
        public DynamicJsonNodeModule dynamicJsonNodeModule(
            BeanFactory beanFactory,
            @Qualifier("DefaultCraneJacksonObjectMapper") ObjectMapper defaultObjectMapper,
            @Qualifier("DefaultCraneJacksonOperatorFactory") OperatorFactory defaultOperatorFactory,
            @Qualifier("DefaultCraneBeanOperateConfigurationParser") OperateConfigurationParser<? extends OperationConfiguration> defaultOperateConfigurationParser,
            @Qualifier("DefaultCraneUnorderedOperationExecutor") OperationExecutor defaultOperationExecutor) {
            return new DynamicJsonNodeModule(
                beanFactory, defaultObjectMapper, defaultOperatorFactory, defaultOperateConfigurationParser, defaultOperationExecutor
            );
        }

        @Order
        @Bean("DefaultCraneJacksonSerializeObjectMapper")
        public ObjectMapper serializeObjectMapper(DynamicJsonNodeModule dynamicJsonNodeModule) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(dynamicJsonNodeModule);
            return objectMapper;
        }

    }

}
