package top.xiajibagao.crane.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import top.xiajibagao.crane.annotation.extend.ContainerMethodBean;
import top.xiajibagao.crane.container.EnumDictContainer;
import top.xiajibagao.crane.container.KeyValueContainer;
import top.xiajibagao.crane.extend.cache.SimpleCacheManager;
import top.xiajibagao.crane.extend.container.MethodContainer;
import top.xiajibagao.crane.helper.CollUtils;
import top.xiajibagao.crane.helper.EnumDict;
import top.xiajibagao.crane.impl.bean.BeanReflexOperatorFactory;
import top.xiajibagao.crane.impl.json.JacksonOperatorFactory;
import top.xiajibagao.crane.operator.SequentialOperationExecutor;
import top.xiajibagao.crane.operator.UnorderedOperationExecutor;
import top.xiajibagao.crane.parse.BeanOperateConfigurationParser;

import java.util.Map;

/**
 * 默认配置
 *
 * @author huangchengxing
 * @date 2022/03/03 13:36
 */
public class DefaultCraneConfig {

    // ==================== 解析器 ====================

    @Order
    @ConditionalOnMissingBean(BeanOperateConfigurationParser.class)
    @Bean("DefaultCraneSimpleCacheManager")
    public SimpleCacheManager simpleCacheManager() {
        return new SimpleCacheManager();
    }

    @Order
    @ConditionalOnMissingBean(BeanOperateConfigurationParser.class)
    @Bean("DefaultCraneBeanOperateConfigurationParser")
    public BeanOperateConfigurationParser beanOperateConfigurationParser(CraneGlobalConfiguration configuration, BeanFactory beanFactory) {
        return new BeanOperateConfigurationParser(configuration, beanFactory);
    }

    // ==================== 操作者 ====================

    @Order
    @ConditionalOnMissingBean(ObjectMapper.class)
    @Bean("DefaultCraneObjectMapper")
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    @Order
    @ConditionalOnMissingBean(JacksonOperatorFactory.class)
    @Bean("DefaultCraneJacksonOperatorFactory")
    public JacksonOperatorFactory jacksonOperatorFactory(ObjectMapper objectMapper) {
        return new JacksonOperatorFactory(objectMapper);
    }

    @Order
    @ConditionalOnMissingBean(BeanReflexOperatorFactory.class)
    @Bean("DefaultCraneBeanReflexOperatorFactory")
    public BeanReflexOperatorFactory reflexOperatorFactory() {
        return new BeanReflexOperatorFactory();
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
    @ConditionalOnMissingBean(MethodContainer.class)
    @Bean("DefaultCraneKeyMethodContainer")
    public MethodContainer methodContainer(ApplicationContext applicationContext) {
        MethodContainer container = new MethodContainer();
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(ContainerMethodBean.class);
        CollUtils.foreach(beans, (name, bean) -> container.register(bean));
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

}
