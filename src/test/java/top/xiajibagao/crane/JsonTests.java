package top.xiajibagao.crane;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import top.xiajibagao.crane.container.EnumDictContainer;
import top.xiajibagao.crane.container.KeyValueContainer;
import top.xiajibagao.crane.impl.bean.BeanReflexOperatorFactory;
import top.xiajibagao.crane.impl.json.JacksonOperatorFactory;
import top.xiajibagao.crane.model.JsonPerson;
import top.xiajibagao.crane.operator.interfaces.OperationExecutor;
import top.xiajibagao.crane.parse.BeanOperateConfigurationParser;
import top.xiajibagao.crane.parse.interfaces.OperationConfiguration;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class JsonTests {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JacksonOperatorFactory jacksonOperatorFactory;
    @Autowired
    BeanReflexOperatorFactory beanReflexOperatorFactory;

    @Autowired
    EnumDictContainer enumDictContainer;
    @Autowired
    KeyValueContainer keyValueContainer;

    @Autowired
    BeanOperateConfigurationParser configurationParser;

    @Qualifier("UnorderedOperationExecutor")
    @Autowired
    OperationExecutor unorderedOperationExecutor;

    @Qualifier("SequentialOperationExecutor")
    @Autowired
    OperationExecutor sequentialOperationExecutor;

    @BeforeEach
    @PostConstruct
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
    void testSimpleJsonKV() {
        JsonPerson jsonPerson = new JsonPerson()
            .setSex(0);
        JsonNode jsonNode = objectMapper.valueToTree(jsonPerson);

        OperationConfiguration jsonConfig = configurationParser.parse(JsonPerson.class, jacksonOperatorFactory);
        System.out.println(jsonNode);
        unorderedOperationExecutor.execute(Collections.singletonList(jsonNode), jsonConfig);
        System.out.println(jsonNode);
    }

    @Test
    void testSimpleJsonEnum() {
        JsonPerson scoreDO = new JsonPerson()
            .setName("小明")
            .setGender(Gender.MALE);
        JsonNode jsonNode = objectMapper.valueToTree(scoreDO);

        OperationConfiguration jsonConfig = configurationParser.parse(JsonPerson.class, jacksonOperatorFactory);
        System.out.println(jsonNode);
        unorderedOperationExecutor.execute(Collections.singletonList(jsonNode), jsonConfig);
        System.out.println(jsonNode);
    }

    @Test
    void testSimpleJsonBean() {
        JsonPerson jsonPerson = new JsonPerson().setId(1).setGender(Gender.MALE).setSex(1);
        JsonNode jsonNode = objectMapper.valueToTree(jsonPerson);

        OperationConfiguration jsonConfig = configurationParser.parse(JsonPerson.class, jacksonOperatorFactory);
        System.out.println(jsonNode);
        unorderedOperationExecutor.execute(Collections.singletonList(jsonNode), jsonConfig);
        System.out.println(jsonNode);
    }

    @Test
    void testNested() {
        JsonPerson jsonPerson = new JsonPerson().setName("小明").setSex(1);
        jsonPerson.setRelatives(Arrays.asList(
            new JsonPerson().setName("小明爸").setSex(1),
            new JsonPerson().setName("小明妈").setSex(0)
        ));

        JsonNode jsonNode = objectMapper.valueToTree(jsonPerson);

        OperationConfiguration jsonConfig = configurationParser.parse(JsonPerson.class, jacksonOperatorFactory);
        System.out.println(jsonNode);
        sequentialOperationExecutor.execute(Collections.singletonList(jsonNode), jsonConfig);
        System.out.println(jsonNode);
    }
}
