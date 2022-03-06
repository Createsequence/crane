package top.xiajibagao.crane;

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
import top.xiajibagao.crane.model.BeanPerson;
import top.xiajibagao.crane.operator.interfaces.OperationExecutor;
import top.xiajibagao.crane.parse.BeanOperateConfigurationParser;
import top.xiajibagao.crane.parse.interfaces.OperationConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huangchengxing
 * @date 2022/03/06 14:27
 */
@SpringBootTest
public class BeanTests {

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
    void testSimpleBeanKV() {
        BeanPerson beanPerson = new BeanPerson()
            .setSex(0);
        OperationConfiguration beanConfig = configurationParser.parse(BeanPerson.class, beanReflexOperatorFactory);
        System.out.println(beanPerson);
        unorderedOperationExecutor.execute(Collections.singletonList(beanPerson), beanConfig);
        System.out.println(beanPerson);
    }

    @Test
    void testSimpleBeanEnum() {
        BeanPerson person = new BeanPerson()
            .setName("小明")
            .setGender(Gender.MALE);

        OperationConfiguration beanConfig = configurationParser.parse(BeanPerson.class, beanReflexOperatorFactory);
        System.out.println(person);
        unorderedOperationExecutor.execute(Collections.singletonList(person), beanConfig);
        System.out.println(person);
    }

    @Test
    void testSimpleBean() {
        BeanPerson beanPerson = new BeanPerson().setId(1).setGender(Gender.MALE).setSex(1);

        OperationConfiguration beanConfig = configurationParser.parse(BeanPerson.class, beanReflexOperatorFactory);
        System.out.println(beanPerson);
        unorderedOperationExecutor.execute(Collections.singletonList(beanPerson), beanConfig);
        System.out.println(beanPerson);
    }

    @Test
    void testNested() {
        BeanPerson person = new BeanPerson().setName("小明").setSex(1);
        person.setRelatives(Arrays.asList(
            new BeanPerson().setName("小明爸").setSex(1).setId(1),
            new BeanPerson().setName("小明妈").setSex(0)
        ));

        OperationConfiguration beanConfig = configurationParser.parse(BeanPerson.class, beanReflexOperatorFactory);
        System.out.println(person);
        sequentialOperationExecutor.execute(Collections.singletonList(person), beanConfig);
        System.out.println(person);
    }
}
