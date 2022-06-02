package top.xiajibagao.crane.starter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ClassUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import top.xiajibagao.crane.core.annotation.MethodSourceBean;
import top.xiajibagao.crane.core.aop.MethodResultProcessAspect;
import top.xiajibagao.crane.core.cache.ConfigurationCache;
import top.xiajibagao.crane.core.cache.OperationConfigurationCache;
import top.xiajibagao.crane.core.component.AsmReflexBeanPropertyFactory;
import top.xiajibagao.crane.core.component.BeanPropertyFactory;
import top.xiajibagao.crane.core.component.ReflexBeanPropertyFactory;
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
import java.util.Set;

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

    @Primary
    @Order
    @ConditionalOnMissingBean(BeanPropertyFactory.class)
    @Bean("CraneDefaultBeanPropertyFactory")
    public BeanPropertyFactory beanPropertyFactory(CraneAutoConfigurationProperties craneAutoConfigurationProperties) {
        return craneAutoConfigurationProperties.isEnableAsmReflect() ?
            new AsmReflexBeanPropertyFactory() : new ReflexBeanPropertyFactory();
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

    @Primary
    @Order
    @ConditionalOnMissingBean(CombineOperationConfigurationParser.class)
    @Bean("DefaultCraneCombineOperationConfigurationParser")
    public CombineOperationConfigurationParser combineOperationConfigurationParser(Collection<OperateConfigurationParser> parsers) {
        CombineOperationConfigurationParser parser = new CombineOperationConfigurationParser();
        parsers.forEach(parser::addParser);
        return parser;
    }

    // ==================== 操作者 ====================

    @Primary
    @Order
    @ConditionalOnMissingBean(BeanReflexOperateHandlerChain.class)
    @Bean("DefaultCraneBeanReflexOperateHandlerChain")
    public BeanReflexOperateHandlerChain beanReflexOperateHandlerChain(BeanPropertyFactory beanPropertyFactory) {
        BeanReflexOperateHandlerChain operateHandlerChain = new ExpressibleBeanReflexOperateHandlerChain(StandardEvaluationContext::new);
        operateHandlerChain.addHandler(new MapOperateHandler())
            .addHandler(new CollectionOperateHandler(operateHandlerChain))
            .addHandler(new ArrayOperateHandler(operateHandlerChain))
            .addHandler(new MapOperateHandler())
            .addHandler(new BeanOperateHandler(beanPropertyFactory));
        log.info(
            "注册处理器链 {}, 已配置节点: {}",
            "DefaultCraneBeanReflexOperateHandlerChain",
            CollUtil.join(operateHandlerChain.handlers(), ", ", h -> h.getClass().getName())
        );
        return operateHandlerChain;
    }

    @Order
    @ConditionalOnMissingBean(BeanReflexAssembler.class)
    @Bean("DefaultCraneBeanReflexAssembler")
    public BeanReflexAssembler beanReflexAssembler(@Qualifier("DefaultCraneBeanReflexOperateHandlerChain") OperateHandlerChain assembleHandlerChain) {
        return new BeanReflexAssembler(assembleHandlerChain);
    }

    @Order
    @ConditionalOnMissingBean(BeanReflexDisassembler.class)
    @Bean("DefaultCraneBeanReflexDisassembler")
    public BeanReflexDisassembler beanReflexDisassembler(@Qualifier("DefaultCraneBeanReflexOperateHandlerChain") OperateHandlerChain assembleHandlerChain) {
        return new BeanReflexDisassembler(assembleHandlerChain);
    }

    // ==================== 容器 ====================

    @Primary
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
        logContainerRegistered(EnumDictContainer.class);
        return new EnumDictContainer(enumDict);
    }

    @Order
    @ConditionalOnMissingBean(KeyValueContainer.class)
    @Bean("DefaultCraneKeyValueContainer")
    public KeyValueContainer simpleKeyValueContainer() {
        logContainerRegistered(KeyValueContainer.class);
        return new KeyValueContainer();
    }

    @Order
    @ConditionalOnMissingBean(MethodSourceContainer.class)
    @Bean("DefaultCraneMethodSourceContainer")
    public MethodSourceContainer methodSourceContainer(ApplicationContext applicationContext, BeanPropertyFactory beanPropertyFactory) {
        logContainerRegistered(MethodSourceContainer.class);
        MethodSourceContainer container = new MethodSourceContainer(beanPropertyFactory);
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
        logContainerRegistered(BeanIntrospectContainer.class);
        return new BeanIntrospectContainer();
    }

    @Order
    @ConditionalOnMissingBean(KeyIntrospectContainer.class)
    @Bean("DefaultCraneKeyIntrospectContainer")
    public KeyIntrospectContainer keyIntrospectContainer() {
        logContainerRegistered(KeyIntrospectContainer.class);
        return new KeyIntrospectContainer();
    }

    // ==================== 执行器 ====================


    @Primary
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

    @Primary
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

    @Component
    @RequiredArgsConstructor
    public static class AfterConfigurationInitedRunner implements ApplicationRunner {

        private final BeanFactory beanFactory;
        private final CraneAutoConfigurationProperties properties;
        private final ConfigurationCache configurationCache;
        private final EnumDict enumDict;

        @Override
        public void run(ApplicationArguments args) {
            preRegisteredEnum();
            if (properties.getCache().isEnablePreParseClass()) {
                preParsedAndCacheClassOperationConfiguration();
            }
        }

        /**
         * 扫描枚举并注册到枚举字典
         */
        private void preRegisteredEnum() {
            properties.getEnums().getDictEnumPackages().stream()
                .map(ClassUtil::scanPackage)
                .flatMap(Collection::stream)
                .filter(Class::isEnum)
                .map(Class.class::cast)
                .forEach(enumDict::register);
        }

        /**
         * 解析配置并加入缓存
         */
        private void preParsedAndCacheClassOperationConfiguration() {
            CraneAutoConfigurationProperties.CacheConfigProperties cacheConfigProperties = properties.getCache();
            Map<String, Set<String>> parserAndPreParsedClassPackages = cacheConfigProperties.getParserAndPreParsedClassPackages();
            parserAndPreParsedClassPackages.forEach((parserName, packages) -> {
                OperateConfigurationParser parser = beanFactory.getBean(OperateConfigurationParser.class, parserName);
                cacheClassOperationConfiguration(parser, packages);
            });
            Set<String> preParsedClassPackages = cacheConfigProperties.getPreParsedClassPackages();
            OperateConfigurationParser parser = beanFactory.getBean(OperateConfigurationParser.class);
            cacheClassOperationConfiguration(parser, preParsedClassPackages);
        }

        private void cacheClassOperationConfiguration(OperateConfigurationParser parser, Set<String> packages) {
            String cacheName = parser.getClass().getName();
            packages.stream()
                .map(ClassUtil::scanPackage)
                .flatMap(Collection::stream)
                .distinct()
                .map(parser::parse)
                .peek(c -> log.info("缓存预解析配置[{}]", c.getTargetClass()))
                .forEach(conf -> configurationCache.setConfigurationCache(cacheName, conf.getTargetClass(), conf));
        }
    }

    private void logContainerRegistered(Class<?> containerClass) {
        log.info("注册容器：{}", containerClass);
    }

}
