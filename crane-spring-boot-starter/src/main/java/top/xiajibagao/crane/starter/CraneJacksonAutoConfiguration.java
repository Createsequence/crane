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
    @ConditionalOnBean(name = CRANE_INNER_OBJECT_MAPPER)
    @ConditionalOnMissingBean(OrderlyOperateHandlerChain.class)
    @Bean("DefaultCraneJacksonOrderlyOperateHandlerChain")
    public OperateHandlerChain orderlyOperateHandlerChain(@Qualifier(CRANE_INNER_OBJECT_MAPPER) ObjectMapper objectMapper) {
        OrderlyOperateHandlerChain operateHandlerChain = new OrderlyOperateHandlerChain();
        operateHandlerChain.addHandler(new ArrayNodeOperateHandler(objectMapper, operateHandlerChain))
            .addHandler(new ObjectNodeOperateHandler(objectMapper))
            .addHandler(new ValueNodeOperateHandler(objectMapper));
        log.info("注册处理器链 {}, 已配置节点: {}", "DefaultCraneJacksonOrderlyOperateHandlerChain", CollUtil.join(operateHandlerChain.handlers(), ", ", h -> h.getClass()
            .getName()));
        return new ExpressibleOperateHandlerChain(operateHandlerChain, StandardEvaluationContext::new);
    }

    @Order
    @ConditionalOnBean(name = CRANE_INNER_OBJECT_MAPPER)
    @ConditionalOnMissingBean(JacksonAssembler.class)
    @Bean("DefaultCraneJacksonAssembler")
    public JacksonAssembler jacksonAssembler(@Qualifier(CRANE_INNER_OBJECT_MAPPER) ObjectMapper objectMapper, @Qualifier("DefaultCraneJacksonOrderlyOperateHandlerChain") OperateHandlerChain assembleHandlerChain) {
        return new JacksonAssembler(objectMapper, assembleHandlerChain);
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
