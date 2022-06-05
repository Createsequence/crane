package top.xiajibagao.crane.starter;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import top.xiajibagao.crane.core.handler.ExpressionPreprocessingInterceptor;
import top.xiajibagao.crane.core.handler.interfaces.SourceOperateInterceptor;
import top.xiajibagao.crane.jackson.impl.handler.ArrayNodeOperateHandler;
import top.xiajibagao.crane.jackson.impl.handler.JacksonOperateHandlerChain;
import top.xiajibagao.crane.jackson.impl.handler.ObjectNodeOperateHandler;
import top.xiajibagao.crane.jackson.impl.handler.ValueNodeOperateHandler;
import top.xiajibagao.crane.jackson.impl.helper.JsonNodeAccessor;
import top.xiajibagao.crane.jackson.impl.module.DynamicJsonNodeModule;
import top.xiajibagao.crane.jackson.impl.operator.JacksonAssembler;
import top.xiajibagao.crane.jackson.impl.operator.JacksonDisassembler;

import java.util.Collections;
import java.util.List;

/**
 * @author huangchengxing
 * @date 2022/05/23 11:01
 */
@Slf4j
@AutoConfigureAfter(CraneAutoConfiguration.class)
@ConditionalOnClass({JacksonAssembler.class, JacksonDisassembler.class})
@Configuration
public class CraneJacksonAutoConfiguration {

    public static final String CRANE_INNER_OBJECT_MAPPER = "CraneInnerObjectMapper";
    
    /**
     * 默认的ObjectMapper实例，用于crane的Jackson模块相关组件读写JsonNode。
     * <b>该实例不能用于注册{@link DynamicJsonNodeModule}</b>
     * 其余配置应当与用于注册{@link DynamicJsonNodeModule}的实例保持一致
     *
     * @return com.fasterxml.jackson.databind.ObjectMapper
     * @author huangchengxing
     * @date 2022/5/24 12:28
     */
    @Order
    @ConditionalOnMissingBean(value = ObjectMapper.class, name = CRANE_INNER_OBJECT_MAPPER)
    @Bean(CRANE_INNER_OBJECT_MAPPER)
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    @Order
    @ConditionalOnMissingBean(ExpressionPreprocessingInterceptor.ContextFactory.class)
    @Bean("DefaultCraneExpressionPreprocessingInterceptorContextFactory")
    public ExpressionPreprocessingInterceptor.ContextFactory expressionContextFactory(@Qualifier(CRANE_INNER_OBJECT_MAPPER) ObjectMapper objectMapper) {
        return new ExpressionPreprocessingInterceptor.DefaultContextFactory(Collections.singletonList(
            context -> context.addPropertyAccessor(new JsonNodeAccessor(objectMapper))
        ));
    }

    @Order
    @ConditionalOnBean(name = CRANE_INNER_OBJECT_MAPPER)
    @ConditionalOnMissingBean(JacksonOperateHandlerChain.class)
    @Bean("DefaultCraneJacksonOperateHandlerChain")
    public JacksonOperateHandlerChain jacksonOperateHandlerChain(@Qualifier(CRANE_INNER_OBJECT_MAPPER) ObjectMapper objectMapper, List<SourceOperateInterceptor> interceptors) {
        JacksonOperateHandlerChain operateHandlerChain = new JacksonOperateHandlerChain();
        interceptors.forEach(operateHandlerChain::addInterceptor);
        operateHandlerChain.addHandler(new ArrayNodeOperateHandler(objectMapper, operateHandlerChain))
            .addHandler(new ObjectNodeOperateHandler(objectMapper))
            .addHandler(new ValueNodeOperateHandler(objectMapper));
        log.info("注册处理器链 {}, 已配置节点: {}", "DefaultCraneJacksonOrderlyOperateHandlerChain", CollUtil.join(operateHandlerChain.handlers(), ", ", h -> h.getClass()
            .getName()));
        return operateHandlerChain;
    }

    @Order
    @ConditionalOnBean(name = CRANE_INNER_OBJECT_MAPPER)
    @ConditionalOnMissingBean(JacksonAssembler.class)
    @Bean("DefaultCraneJacksonAssembler")
    public JacksonAssembler jacksonAssembler(@Qualifier(CRANE_INNER_OBJECT_MAPPER) ObjectMapper objectMapper, JacksonOperateHandlerChain operateHandlerChain) {
        return new JacksonAssembler(objectMapper, operateHandlerChain);
    }

    @Order
    @ConditionalOnBean(name = CRANE_INNER_OBJECT_MAPPER)
    @ConditionalOnMissingBean(JacksonDisassembler.class)
    @Bean("DefaultCraneJacksonDisassembler")
    public JacksonDisassembler jacksonDisassembler(@Qualifier(CRANE_INNER_OBJECT_MAPPER) ObjectMapper objectMapper) {
        return new JacksonDisassembler(objectMapper);
    }

    @Order
    @ConditionalOnBean(name = CRANE_INNER_OBJECT_MAPPER)
    @Bean("DefaultCraneJacksonDynamicJsonNodeModule")
    public DynamicJsonNodeModule dynamicJsonNodeModule(BeanFactory beanFactory, @Qualifier(CRANE_INNER_OBJECT_MAPPER) ObjectMapper objectMapper) {
        return new DynamicJsonNodeModule(beanFactory, objectMapper);
    }

}
