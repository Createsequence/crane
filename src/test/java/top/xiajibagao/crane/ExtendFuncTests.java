package top.xiajibagao.crane;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import top.xiajibagao.crane.config.TestService;
import top.xiajibagao.crane.container.EnumDictContainer;
import top.xiajibagao.crane.container.KeyValueContainer;
import top.xiajibagao.crane.impl.bean.BeanReflexOperatorFactory;
import top.xiajibagao.crane.impl.json.JacksonOperatorFactory;
import top.xiajibagao.crane.model.BeanPerson;
import top.xiajibagao.crane.model.ExtendBeanPerson;
import top.xiajibagao.crane.operator.interfaces.OperationExecutor;
import top.xiajibagao.crane.parse.BeanOperateConfigurationParser;
import top.xiajibagao.crane.parse.interfaces.OperationConfiguration;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huangchengxing
 * @date 2022/03/23 23:16
 */
@SpringBootTest
class ExtendFuncTests {

    @Autowired
    TestService testService;
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    EnumDictContainer enumDictContainer;
    @Autowired
    KeyValueContainer keyValueContainer;
    
    @Autowired
    BeanOperateConfigurationParser configurationParser;
    
    @Autowired
    JacksonOperatorFactory jacksonOperatorFactory;
    @Autowired
    BeanReflexOperatorFactory beanReflexOperatorFactory;
    
    @Qualifier("DefaultCraneUnorderedOperationExecutor")
    @Autowired
    OperationExecutor unorderedOperationExecutor;

    @BeforeEach
    public void initDate() {
        // 初始化键值对容器
        Map<String, Object> gender = new HashMap<>();
        gender.put("0", "女");
        gender.put("1", "男");
        keyValueContainer.register("sex", gender);
        // 初始化枚举容器
        enumDictContainer.register(Gender.class, "sex", Enum::name);
    }

    @Test
    void testMethodResultProcess() throws JsonProcessingException {
        List<BeanPerson> processedBeans = testService.listBeanPerson("true");
        System.out.println(objectMapper.writeValueAsString(processedBeans));
        List<BeanPerson> unprocessedBeans = testService.listBeanPerson("false");
        System.out.println(objectMapper.writeValueAsString(unprocessedBeans));
    }
    
    @Test
    void testMethodContainer() throws JsonProcessingException {
        ExtendBeanPerson extendBeanPerson = new ExtendBeanPerson().setId(1);
        OperationConfiguration beanConfig = configurationParser.parse(ExtendBeanPerson.class, beanReflexOperatorFactory);
        System.out.println(objectMapper.writeValueAsString(extendBeanPerson));
        unorderedOperationExecutor.execute(Collections.singletonList(extendBeanPerson), beanConfig);
        System.out.println(objectMapper.writeValueAsString(extendBeanPerson));
    }

}
