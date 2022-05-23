package top.xiajibagao.crane.starter;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import top.xiajibagao.crane.core.annotation.MethodSourceBean;
import top.xiajibagao.crane.core.aop.MethodResultProcessAspect;
import top.xiajibagao.crane.core.cache.ConfigurationCache;
import top.xiajibagao.crane.core.cache.OperationConfigurationCache;
import top.xiajibagao.crane.core.container.*;
import top.xiajibagao.crane.core.executor.OperationExecutor;
import top.xiajibagao.crane.core.executor.SequentialOperationExecutor;
import top.xiajibagao.crane.core.executor.UnorderedOperationExecutor;
import top.xiajibagao.crane.core.handler.*;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandlerChain;
import top.xiajibagao.crane.core.helper.EnumDict;
import top.xiajibagao.crane.core.helper.OperateTemplate;
import top.xiajibagao.crane.core.operator.BeanReflexAssembler;
import top.xiajibagao.crane.core.operator.BeanReflexDisassembler;
import top.xiajibagao.crane.core.parser.BeanGlobalConfiguration;
import top.xiajibagao.crane.core.parser.ClassAnnotationConfigurationParser;
import top.xiajibagao.crane.core.parser.CombineOperationConfigurationParser;
import top.xiajibagao.crane.core.parser.FieldAnnotationConfigurationParser;
import top.xiajibagao.crane.core.parser.interfaces.GlobalConfiguration;
import top.xiajibagao.crane.core.parser.interfaces.OperateConfigurationParser;

import java.util.Collection;
import java.util.Map;

/**
 * 默认配置
 *
 * @author huangchengxing
 * @date 2022/03/03 13:36
 */
@AutoConfigureAfter(CraneAutoConfigurationProperties.class)
@Slf4j
@Configuration
public class CraneAutoConfiguration {

    @Order
    @ConditionalOnMissingBean(GlobalConfiguration.class)
    @Bean("CraneDefaultGlobalConfiguration")
    public GlobalConfiguration globalConfiguration(CraneAutoConfigurationProperties craneAutoConfigurationProperties) {
        return new BeanGlobalConfiguration();
    }

    // ==================== 解析器 ====================

    @Order
    @ConditionalOnMissingBean(FieldAnnotationConfigurationParser.class)
    @Bean("DefaultCraneFieldAnnotationConfigurationParser")
    public FieldAnnotationConfigurationParser fieldAnnotationConfigurationParser(GlobalConfiguration configuration, BeanFactory beanFactory) {
        return new FieldAnnotationConfigurationParser(configuration, beanFactory);
    }

    @Order
    @ConditionalOnMissingBean(ClassAnnotationConfigurationParser.class)
    @Bean("DefaultCraneClassAnnotationConfigurationParser")
    public ClassAnnotationConfigurationParser classAnnotationConfigurationParser(GlobalConfiguration configuration, BeanFactory beanFactory) {
        return new ClassAnnotationConfigurationParser(configuration, beanFactory);
    }

    @Order
    @ConditionalOnMissingBean(CombineOperationConfigurationParser.class)
    @Bean("DefaultCraneCombineOperationConfigurationParser")
    public CombineOperationConfigurationParser classAnnotationConfigurationParser(Collection<OperateConfigurationParser> parsers) {
        CombineOperationConfigurationParser parser = new CombineOperationConfigurationParser();
        parsers.forEach(parser::addParser);
        return parser;
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
        log.info(
            "注册处理器链 {}, 已配置节点: {}",
            "DefaultCraneOrderlyOperateHandlerChain",
            CollUtil.join(operateHandlerChain.handlers(), ", ", h -> h.getClass().getName())
        );
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
        log.info("注册容器：{}", EnumDictContainer.class);
        return new EnumDictContainer(enumDict);
    }

    @Order
    @ConditionalOnMissingBean(KeyValueContainer.class)
    @Bean("DefaultCraneKeyValueContainer")
    public KeyValueContainer simpleKeyValueContainer() {
        log.info("注册容器：{}", KeyValueContainer.class);
        return new KeyValueContainer();
    }

    @Order
    @ConditionalOnMissingBean(MethodSourceContainer.class)
    @Bean("DefaultCraneMethodSourceContainer")
    public MethodSourceContainer methodSourceContainer(ApplicationContext applicationContext) {
        log.info("注册容器：{}", MethodSourceContainer.class);
        MethodSourceContainer container = new MethodSourceContainer();
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(MethodSourceBean.class);
        if (CollUtil.isNotEmpty(beans)) {
            beans.forEach((name, bean) -> container.register(bean));
        }
        return container;
    }

    @Order
    @ConditionalOnMissingBean(BeanIntrospectContainer.class)
    @Bean("DefaultCraneBeanIntrospectContainer")
    public BeanIntrospectContainer introspectContainer() {
        log.info("注册容器：{}", BeanIntrospectContainer.class);
        return new BeanIntrospectContainer();
    }

    @Order
    @ConditionalOnMissingBean(KeyIntrospectContainer.class)
    @Bean("DefaultCraneKeyIntrospectContainer")
    public KeyIntrospectContainer keyIntrospectContainer() {
        log.info("注册容器：{}", KeyIntrospectContainer.class);
        return new KeyIntrospectContainer();
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

    // ==================== 辅助类 ====================

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
        log.info("启用切面：{}", MethodResultProcessAspect.class);
        return new MethodResultProcessAspect(beanFactory, configurationCache);
    }

    @Order
    @ConditionalOnMissingBean(OperateTemplate.class)
    @Bean("DefaultCraneOperateTemplate")
    public OperateTemplate operateTemplate(
        @Qualifier("DefaultCraneOperationConfigurationCache") ConfigurationCache configurationCache,
        @Qualifier("DefaultCraneFieldAnnotationConfigurationParser") OperateConfigurationParser defaultOperateConfigurationParser,
        @Qualifier("DefaultCraneUnorderedOperationExecutor") OperationExecutor defaultOperationExecutor) {
        return new OperateTemplate(configurationCache, defaultOperateConfigurationParser, defaultOperationExecutor);
    }

}
