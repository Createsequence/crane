package top.xiajibagao.crane.starter;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import top.xiajibagao.crane.core.handler.ExpressibleOperateHandlerChain;
import top.xiajibagao.crane.core.handler.OrderlyOperateHandlerChain;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandlerChain;
import top.xiajibagao.crane.jackson.impl.handler.ArrayNodeOperateHandler;
import top.xiajibagao.crane.jackson.impl.handler.ObjectNodeOperateHandler;
import top.xiajibagao.crane.jackson.impl.handler.ValueNodeOperateHandler;
import top.xiajibagao.crane.jackson.impl.module.DynamicJsonNodeModule;
import top.xiajibagao.crane.jackson.impl.operator.JacksonAssembler;
import top.xiajibagao.crane.jackson.impl.operator.JacksonDisassembler;

/**
 * @author huangchengxing
 * @date 2022/05/23 11:01
 */
@Slf4j
@AutoConfigureAfter(CraneAutoConfiguration.class)
@ConditionalOnClass({JacksonAssembler.class, JacksonDisassembler.class})
@Configuration
public class CraneJacksonAutoConfiguration {

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
        OrderlyOperateHandlerChain operateHandlerChain = new OrderlyOperateHandlerChain();
        operateHandlerChain.addHandler(new ArrayNodeOperateHandler(objectMapper, operateHandlerChain))
            .addHandler(new ObjectNodeOperateHandler(objectMapper))
            .addHandler(new ValueNodeOperateHandler(objectMapper));
        log.info("注册处理器链 {}, 已配置节点: {}", "DefaultCraneJacksonOrderlyOperateHandlerChain", CollUtil.join(operateHandlerChain.handlers(), ", ", h -> h.getClass()
            .getName()));
        return new ExpressibleOperateHandlerChain(operateHandlerChain, StandardEvaluationContext::new);
    }

    @Order
    @ConditionalOnMissingBean(JacksonAssembler.class)
    @Bean("DefaultCraneJacksonAssembler")
    public JacksonAssembler jacksonAssembler(@Qualifier("DefaultCraneJacksonObjectMapper") ObjectMapper objectMapper, @Qualifier("DefaultCraneJacksonOrderlyOperateHandlerChain") OperateHandlerChain assembleHandlerChain) {
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
    public DynamicJsonNodeModule dynamicJsonNodeModule(BeanFactory beanFactory, @Qualifier("DefaultCraneJacksonObjectMapper") ObjectMapper defaultObjectMapper) {
        return new DynamicJsonNodeModule(beanFactory, defaultObjectMapper);
    }

    @Order
    @Bean("DefaultCraneJacksonSerializeObjectMapper")
    public ObjectMapper serializeObjectMapper(@Qualifier("DefaultCraneJacksonDynamicJsonNodeModule") DynamicJsonNodeModule dynamicJsonNodeModule) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(dynamicJsonNodeModule);
        return objectMapper;
    }

}
