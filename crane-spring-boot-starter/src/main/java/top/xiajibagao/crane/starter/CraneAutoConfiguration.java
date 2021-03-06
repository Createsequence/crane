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
import org.springframework.stereotype.Component;
import top.xiajibagao.crane.core.annotation.MethodSourceBean;
import top.xiajibagao.crane.core.aop.MethodResultProcessAspect;
import top.xiajibagao.crane.core.cache.ConfigurationCache;
import top.xiajibagao.crane.core.cache.OperationConfigurationCache;
import top.xiajibagao.crane.core.container.*;
import top.xiajibagao.crane.core.executor.OperationExecutor;
import top.xiajibagao.crane.core.executor.SequentialOperationExecutor;
import top.xiajibagao.crane.core.executor.UnorderedOperationExecutor;
import top.xiajibagao.crane.core.handler.*;
import top.xiajibagao.crane.core.helper.EnumDict;
import top.xiajibagao.crane.core.helper.OperateTemplate;
import top.xiajibagao.crane.core.helper.property.AsmReflexBeanPropertyFactory;
import top.xiajibagao.crane.core.helper.property.BeanPropertyFactory;
import top.xiajibagao.crane.core.helper.property.ReflexBeanPropertyFactory;
import top.xiajibagao.crane.core.interceptor.ExpressionPreprocessingInterceptor;
import top.xiajibagao.crane.core.operator.BeanReflexAssembler;
import top.xiajibagao.crane.core.operator.BeanReflexDisassembler;
import top.xiajibagao.crane.core.operator.BeanReflexOperateProcessor;
import top.xiajibagao.crane.core.operator.interfaces.*;
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
 * ????????????
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

    // ==================== ????????? ====================

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

    // ==================== ????????? ====================

    @Order
    @ConditionalOnMissingBean(ExpressionPreprocessingInterceptor.ContextFactory.class)
    @Bean("DefaultCraneExpressionPreprocessingInterceptorContextFactory")
    public ExpressionPreprocessingInterceptor.ContextFactory expressionContextFactory() {
        return new ExpressionPreprocessingInterceptor.DefaultContextFactory();
    }

    @Order
    @ConditionalOnMissingBean(ExpressionPreprocessingInterceptor.class)
    @Bean("DefaultCraneExpressionPreprocessingInterceptor")
    public ExpressionPreprocessingInterceptor expressionPreprocessingInterceptor(ExpressionPreprocessingInterceptor.ContextFactory contextFactory) {
        return new ExpressionPreprocessingInterceptor(contextFactory);
    }

    @ConditionalOnMissingBean(NullOperateHandler.class)
    @Bean("DefaultCraneNullOperateHandler")
    public NullOperateHandler nullOperateHandler(@Qualifier("DefaultCraneBeanReflexOperateHandlerChain") OperateProcessor operateProcessor) {
        return new NullOperateHandler(operateProcessor);
    }

    @ConditionalOnMissingBean(MapOperateHandler.class)
    @Bean("DefaultCraneMapOperateHandler")
    public MapOperateHandler mapOperateHandler(@Qualifier("DefaultCraneBeanReflexOperateHandlerChain") OperateProcessor operateProcessor) {
        return new MapOperateHandler(operateProcessor);
    }

    @ConditionalOnMissingBean(BeanOperateHandler.class)
    @Bean("DefaultCraneBeanOperateHandler")
    public BeanOperateHandler beanOperateHandler(@Qualifier("DefaultCraneBeanReflexOperateHandlerChain") OperateProcessor operateProcessor, BeanPropertyFactory beanPropertyFactory) {
        return new BeanOperateHandler(operateProcessor, beanPropertyFactory);
    }

    @ConditionalOnMissingBean(CollectionOperateHandler.class)
    @Bean("DefaultCraneCollectionOperateHandler")
    public CollectionOperateHandler collectionOperateHandler(@Qualifier("DefaultCraneBeanReflexOperateHandlerChain") OperateProcessor operateProcessor) {
        return new CollectionOperateHandler(operateProcessor);
    }

    @ConditionalOnMissingBean(ArrayOperateHandler.class)
    @Bean("DefaultCraneArrayOperateHandler")
    public ArrayOperateHandler arrayOperateHandler(@Qualifier("DefaultCraneBeanReflexOperateHandlerChain") OperateProcessor operateProcessor) {
        return new ArrayOperateHandler(operateProcessor);
    }

    @Primary
    @Order
    @ConditionalOnMissingBean(BeanReflexOperateProcessor.class)
    @Bean("DefaultCraneBeanReflexOperateHandlerChain")
    public BeanReflexOperateProcessor beanReflexOperateHandlerChain() {
        return new BeanReflexOperateProcessor();
    }

    @Order
    @ConditionalOnMissingBean(BeanReflexAssembler.class)
    @Bean("DefaultCraneBeanReflexAssembler")
    public BeanReflexAssembler beanReflexAssembler(@Qualifier("DefaultCraneBeanReflexOperateHandlerChain") OperateProcessor operateProcessor) {
        return new BeanReflexAssembler(operateProcessor);
    }

    @Order
    @ConditionalOnMissingBean(BeanReflexDisassembler.class)
    @Bean("DefaultCraneBeanReflexDisassembler")
    public BeanReflexDisassembler beanReflexDisassembler(@Qualifier("DefaultCraneBeanReflexOperateHandlerChain") OperateProcessor operateProcessor) {
        return new BeanReflexDisassembler(operateProcessor);
    }

    // ==================== ?????? ====================

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

    // ==================== ????????? ====================


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

    // ==================== ????????? ====================

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
        log.info("???????????????{}", MethodResultProcessAspect.class);
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

        private final BeanReflexOperateProcessor beanReflexOperateProcessor;
        private final Collection<SourceReader> sourceReaders;
        private final Collection<SourceReadInterceptor> sourceReadInterceptors;
        private final Collection<TargetWriteInterceptor> targetWriteInterceptors;
        private final Collection<TargetWriter> targetWriters;

        private final ApplicationContext applicationContext;
        private final CraneAutoConfigurationProperties properties;
        private final ConfigurationCache configurationCache;
        private final EnumDict enumDict;

        @Override
        public void run(ApplicationArguments args) {
            // ???????????????
            preRegisteredEnum();
            // ????????????????????????
            preParsedAndCacheClassOperationConfiguration();
            // ?????????????????????????????????OperateProcessor
            initOperateProcessor();
        }

        /**
         * ????????????????????????????????????
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
         * ???????????????????????????
         */
        private void preParsedAndCacheClassOperationConfiguration() {
            if (!properties.getCache().isEnablePreParseClass()) {
                return;
            }
            CraneAutoConfigurationProperties.CacheConfigProperties cacheConfigProperties = properties.getCache();
            Map<String, Set<String>> parserAndPreParsedClassPackages = cacheConfigProperties.getParserAndPreParsedClassPackages();
            parserAndPreParsedClassPackages.forEach((parserName, packages) -> {
                OperateConfigurationParser parser = applicationContext.getBean(parserName, OperateConfigurationParser.class);
                cacheClassOperationConfiguration(parser, packages);
            });
            Set<String> preParsedClassPackages = cacheConfigProperties.getPreParsedClassPackages();
            OperateConfigurationParser parser = applicationContext.getBean(OperateConfigurationParser.class);
            cacheClassOperationConfiguration(parser, preParsedClassPackages);
        }

        private void cacheClassOperationConfiguration(OperateConfigurationParser parser, Set<String> packages) {
            String cacheName = parser.getClass().getName();
            packages.stream()
                .map(ClassUtil::scanPackage)
                .flatMap(Collection::stream)
                .distinct()
                .map(parser::parse)
                .peek(c -> log.info("?????????????????????[{}]", c.getTargetClass()))
                .forEach(conf -> configurationCache.setConfigurationCache(cacheName, conf.getTargetClass(), conf));
        }

        /**
         * ?????????{@link BeanReflexOperateProcessor}??????????????????????????????
         */
        private void initOperateProcessor() {
            ConfigHelper.registerForOperateProcessor(beanReflexOperateProcessor, applicationContext);
        }
    }

    private void logContainerRegistered(Class<?> containerClass) {
        log.info("???????????????{}", containerClass);
    }

}
