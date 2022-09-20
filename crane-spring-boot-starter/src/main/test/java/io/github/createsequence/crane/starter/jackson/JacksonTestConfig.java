package io.github.createsequence.crane.starter.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import io.github.createsequence.crane.jackson.impl.module.DynamicJsonNodeModule;
import io.github.createsequence.crane.starter.CraneJacksonAutoConfiguration;
import io.github.createsequence.crane.starter.EnableCrane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.PostConstruct;

/**
 * @author huangchengxing
 * @date 2022/05/30 14:22
 */
@EnableCrane
@Configuration
public class JacksonTestConfig {

    @Autowired
    @Qualifier(CraneJacksonAutoConfiguration.CRANE_INNER_OBJECT_MAPPER)
    ObjectMapper objectMapper;

    @PostConstruct
    public void initInnerObjectMapper() {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
    }

    @Primary
    @Bean
    public ObjectMapper objectMapper(DynamicJsonNodeModule dynamicJsonNodeModule) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
        objectMapper.registerModule(dynamicJsonNodeModule);
        return objectMapper;
    }

}
