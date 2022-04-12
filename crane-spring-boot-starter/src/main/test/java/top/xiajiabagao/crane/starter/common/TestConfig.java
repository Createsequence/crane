package top.xiajiabagao.crane.starter.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.xiajibagao.crane.starter.EnableCrane;

/**
 * @author huangchengxing
 * @date 2022/04/09 20:25
 */
@EnableCrane
@Configuration
public class TestConfig {

    @Bean("TestObjectMapper")
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    @Bean
    public TestContainer testContainer() {
        return new TestContainer();
    }

}
