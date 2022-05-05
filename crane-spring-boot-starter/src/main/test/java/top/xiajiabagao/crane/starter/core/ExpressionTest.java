package top.xiajiabagao.crane.starter.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.xiajiabagao.crane.starter.common.Gender;
import top.xiajiabagao.crane.starter.common.TestConfig;
import top.xiajiabagao.crane.starter.common.TestContainer;
import top.xiajibagao.crane.core.container.EnumDictContainer;
import top.xiajibagao.crane.core.container.KeyValueContainer;
import top.xiajibagao.crane.core.executor.OperationExecutor;
import top.xiajibagao.crane.core.operator.interfaces.OperatorFactory;
import top.xiajibagao.crane.core.parser.OperateConfigurationAssistant;
import top.xiajibagao.crane.core.parser.interfaces.GlobalConfiguration;
import top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration;
import top.xiajibagao.crane.core.helper.OperateTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 表达式测试
 * 1、测试是字段映射配置的SpEL表达式是否能正确执行；
 * 2、测试是使用顺序执行器时，是否能按照排序正确执行；
 *
 * @author huangchengxing
 * @date 2022/04/13 12:55
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class ExpressionTest {

    @Qualifier("TestObjectMapper")
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OperateTemplate operateTemplate;

    // 容器
    @Autowired
    private EnumDictContainer enumDictContainer;
    @Autowired
    private KeyValueContainer keyValueContainer;
    @Autowired
    private TestContainer testContainer;

    // 解析器
    @Autowired
    private GlobalConfiguration globalConfiguration;
    @Qualifier("DefaultCraneBeanReflexOperatorFactory")
    @Autowired
    private OperatorFactory operatorFactory;
    @Qualifier("DefaultCraneSequentialOperationExecutor")
    @Autowired
    OperationExecutor operationExecutor;

    private static Person getActualPerson() {
        return new Person()
            .setId(1)
            .setSex(1)
            .setGender(Gender.MALE);
    }

    private OperationConfiguration getConfiguration() {
        OperateConfigurationAssistant<Person> assistant = OperateConfigurationAssistant.basedOnBeanOperationConfiguration(
            globalConfiguration, Person.class, operatorFactory
        );
        assistant.buildAssembler(Person::getGender, enumDictContainer)
            .namespace("gender")
            .property("id", Person::getGenderId)
            .property("name", Person::getGenderName)
            .sort(2)
            .build();
        assistant.buildAssembler(Person::getSex, keyValueContainer)
            .namespace("sex")
            .property("", "sexName", "#source == '男' ? 'male' : 'female'", String.class)
            .property("", "name", "#source == '男' ? #target.name + '先生' : #target.name + '女士'", String.class)
            .sort(1)
            .build();
        assistant.buildAssembler(Person::getId, testContainer)
            .property("beanName", "name")
            .property("beanAge", "age")
            .sort(0)
            .build();
        assistant.buildDisassembler(Person::getRelatives, assistant.getConfiguration()).build();
        return assistant.getConfiguration();
    }

    private static Person getExpectedPerson() {
        return new Person()
            .setId(1)
            .setSex(1)
            .setGender(Gender.MALE)
            .setAge(35)
            .setName("小明先生")
            .setSexName("male")
            .setGenderId(Gender.MALE.getId())
            .setGenderName(Gender.MALE.getName());
    }

    private void processAndLog(Object actual) throws JsonProcessingException {
        OperationConfiguration configuration = getConfiguration();
        System.out.println("before: " + objectMapper.writeValueAsString(actual));
        operateTemplate.process(actual, configuration, operationExecutor);
        System.out.println("after: " + objectMapper.writeValueAsString(actual));
    }

    @Before
    public void initDate() {
        // 初始化键值对容器
        Map<String, Object> gender = new HashMap<>();
        gender.put("0", "女");
        gender.put("1", "男");
        keyValueContainer.register("sex", gender);

        // 初始化枚举容器
        enumDictContainer.register(Gender.class, "gender", Enum::name);

        // 初始化自定义容器
        Map<String, Object> mockData = new HashMap<>();
        mockData.put("beanName", "小明");
        mockData.put("beanAge", 35);
        testContainer.getMockDatas()
            .put(1, mockData);
    }

    /**
     * 处理单个普通对象
     */
    @SneakyThrows
    @Test
    public void testSingleBeanByAnnotationConfig() {
        Person actual = getActualPerson();
        processAndLog(actual);

        Person expected = getExpectedPerson();
        Assertions.assertEquals(expected, actual);
    }

    /**
     * 处理多个普通对象
     */
    @SneakyThrows
    @Test
    public void testMultiBeanByAnnotationConfig() {
        Person[] actual = new Person[]{getActualPerson(), getActualPerson()};
        processAndLog(actual);

        Assertions.assertEquals(Arrays.asList(getExpectedPerson(), getExpectedPerson()), Arrays.asList(actual));
    }

    /**
     * 处理单个嵌套对象
     */
    @SneakyThrows
    @Test
    public void testMultiNestBeanByAnnotationConfig() {
        Person actual = getActualPerson()
            .setRelatives(Arrays.asList(getActualPerson(), getActualPerson()));
        processAndLog(actual);

        Person expected = getExpectedPerson()
            .setRelatives(Arrays.asList(getExpectedPerson(), getExpectedPerson()));
        Assertions.assertEquals(expected, actual);
    }

    /**
     * 处理多个嵌套对象
     */
    @SneakyThrows
    @Test
    public void testSingleNestBeanByAnnotationConfig() {
        Person[] actual = new Person[] {
            getActualPerson()
                .setRelatives(Arrays.asList(getActualPerson(), getActualPerson())),
            getActualPerson()
                .setRelatives(Arrays.asList(getActualPerson(), getActualPerson()))
        };
        processAndLog(actual);

        Person[] expected = new Person[] {
            getExpectedPerson()
                .setRelatives(Arrays.asList(getExpectedPerson(), getExpectedPerson())),
            getExpectedPerson()
                .setRelatives(Arrays.asList(getExpectedPerson(), getExpectedPerson()))
        };
        Assertions.assertEquals(Arrays.asList(expected), Arrays.asList(actual));
    }

}
