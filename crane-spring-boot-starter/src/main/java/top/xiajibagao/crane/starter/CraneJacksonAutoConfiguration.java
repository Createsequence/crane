package top.xiajibagao.crane.starter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import top.xiajibagao.crane.core.interceptor.ExpressionPreprocessingInterceptor;
import top.xiajibagao.crane.jackson.impl.handler.ArrayNodeOperateHandler;
import top.xiajibagao.crane.jackson.impl.handler.NullNodeOperateHandler;
import top.xiajibagao.crane.jackson.impl.handler.ObjectNodeOperateHandler;
import top.xiajibagao.crane.jackson.impl.handler.ValueNodeOperateHandler;
import top.xiajibagao.crane.jackson.impl.helper.JsonNodeAccessor;
import top.xiajibagao.crane.jackson.impl.module.DynamicJsonNodeModule;
import top.xiajibagao.crane.jackson.impl.operator.JacksonAssembler;
import top.xiajibagao.crane.jackson.impl.operator.JacksonDisassembler;
import top.xiajibagao.crane.jackson.impl.operator.JacksonOperateProcessor;

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
    @ConditionalOnMissingBean(JacksonOperateProcessor.class)
    @Bean("DefaultCraneJacksonOperateProcessor")
    public JacksonOperateProcessor jacksonOperateProcessor() {
        return new JacksonOperateProcessor();
    }

    @ConditionalOnMissingBean(ValueNodeOperateHandler.class)
    @Bean("DefaultCraneValueNodeOperateHandler")
    public ValueNodeOperateHandler valueNodeOperateHandler(@Qualifier(CRANE_INNER_OBJECT_MAPPER) ObjectMapper objectMapper, JacksonOperateProcessor jacksonOperateProcessor) {
        return new ValueNodeOperateHandler(objectMapper, jacksonOperateProcessor);
    }

    @ConditionalOnMissingBean(ObjectNodeOperateHandler.class)
    @Bean("DefaultCraneObjectNodeOperateHandler")
    public ObjectNodeOperateHandler objectNodeOperateHandler(@Qualifier(CRANE_INNER_OBJECT_MAPPER) ObjectMapper objectMapper, JacksonOperateProcessor jacksonOperateProcessor) {
        return new ObjectNodeOperateHandler(objectMapper, jacksonOperateProcessor);
    }

    @ConditionalOnMissingBean(NullNodeOperateHandler.class)
    @Bean("DefaultCraneNullNodeOperateHandler")
    public NullNodeOperateHandler nullNodeOperateHandler(@Qualifier(CRANE_INNER_OBJECT_MAPPER) ObjectMapper objectMapper, JacksonOperateProcessor jacksonOperateProcessor) {
        return new NullNodeOperateHandler(objectMapper, jacksonOperateProcessor);
    }

    @ConditionalOnMissingBean(ArrayNodeOperateHandler.class)
    @Bean("DefaultCraneArrayNodeOperateHandler")
    public ArrayNodeOperateHandler arrayNodeOperateHandler(@Qualifier(CRANE_INNER_OBJECT_MAPPER) ObjectMapper objectMapper, JacksonOperateProcessor jacksonOperateProcessor) {
        return new ArrayNodeOperateHandler(objectMapper, jacksonOperateProcessor);
    }

    @Order
    @ConditionalOnBean(name = CRANE_INNER_OBJECT_MAPPER)
    @ConditionalOnMissingBean(JacksonAssembler.class)
    @Bean("DefaultCraneJacksonAssembler")
    public JacksonAssembler jacksonAssembler(@Qualifier(CRANE_INNER_OBJECT_MAPPER) ObjectMapper objectMapper, JacksonOperateProcessor jacksonOperateProcessor) {
        return new JacksonAssembler(objectMapper, jacksonOperateProcessor);
    }

    @Order
    @ConditionalOnBean(name = CRANE_INNER_OBJECT_MAPPER)
    @ConditionalOnMissingBean(JacksonDisassembler.class)
    @Bean("DefaultCraneJacksonDisassembler")
    public JacksonDisassembler jacksonDisassembler(@Qualifier(CRANE_INNER_OBJECT_MAPPER) ObjectMapper objectMapper, JacksonOperateProcessor jacksonOperateProcessor) {
        return new JacksonDisassembler(objectMapper, jacksonOperateProcessor);
    }

    @Order
    @ConditionalOnBean(name = CRANE_INNER_OBJECT_MAPPER)
    @Bean("DefaultCraneJacksonDynamicJsonNodeModule")
    public DynamicJsonNodeModule dynamicJsonNodeModule(BeanFactory beanFactory, @Qualifier(CRANE_INNER_OBJECT_MAPPER) ObjectMapper objectMapper) {
        return new DynamicJsonNodeModule(beanFactory, objectMapper);
    }

    @Component
    @RequiredArgsConstructor
    public static class AfterJacksonConfigurationInitedRunner implements ApplicationRunner {

        private final JacksonOperateProcessor jacksonOperateProcessor;
        private final ExpressionPreprocessingInterceptor expressionPreprocessingInterceptor;
        private final ApplicationContext applicationContext;

        @Qualifier(CRANE_INNER_OBJECT_MAPPER)
        private final ObjectMapper objectMapper;

        @Override
        public void run(ApplicationArguments args) {
            initOperateProcessor();
            initContextFactoryFactory();
        }

        /**
         * 为上下文工厂添加{@link JsonNodeAccessor}
         */
        public void initContextFactoryFactory() {
            ExpressionPreprocessingInterceptor.ContextFactory contextFactory = expressionPreprocessingInterceptor.getContextFactory();
            if (contextFactory instanceof ExpressionPreprocessingInterceptor.DefaultContextFactory) {
                ((ExpressionPreprocessingInterceptor.DefaultContextFactory) contextFactory)
                    .addAction(context -> context.addPropertyAccessor(new JsonNodeAccessor(objectMapper)));
            }
        }

        /**
         * 初始化{@link JacksonOperateProcessor}，为其注册必要的组件
         */
        private void initOperateProcessor() {
            ConfigHelper.registerForOperateProcessor(jacksonOperateProcessor, applicationContext);
        }

    }

}
