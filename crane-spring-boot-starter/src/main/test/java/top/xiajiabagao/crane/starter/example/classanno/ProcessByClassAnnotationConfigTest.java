package top.xiajiabagao.crane.starter.example.classanno;

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
import top.xiajiabagao.crane.starter.example.common.Gender;
import top.xiajiabagao.crane.starter.example.common.TestConfig;
import top.xiajiabagao.crane.starter.example.common.TestContainer;
import top.xiajibagao.crane.core.container.EnumDictContainer;
import top.xiajibagao.crane.core.container.KeyValueContainer;
import top.xiajibagao.crane.core.helper.OperateTemplate;
import top.xiajibagao.crane.core.parser.interfaces.OperateConfigurationParser;
import top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试javaBean基于类注解配置
 * 1、是否能正确处理单个/多个的嵌套/非嵌套对象；
 * 2、是否能正确处理字段映射模板；
 * 3、能否正确继承父类以及父接口的配置；
 * 4、能否在key字段不存在时正确使用别名寻找备选key字段；
 * 5、能否正确排除继承指定类的注解；
 *
 * @author huangchengxing
 * @date 2022/04/10 15:15
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class ProcessByClassAnnotationConfigTest {

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
    @Qualifier("DefaultCraneClassAnnotationConfigurationParser")
    @Autowired
    private OperateConfigurationParser operateConfigurationParser;

    private static Person getActualPerson() {
        return new Person()
            .setId(1)
            .setSex(1)
            .setGender(Gender.MALE);
    }

    private static Person getExpectedPerson() {
        return new Person()
            .setId(1)
            .setSex(1)
            .setGender(Gender.MALE)
            .setAge(35)
            .setName("小明")
            .setGenderId(Gender.MALE.getId())
            .setGenderName(Gender.MALE.getName());
    }

    private void processAndLog(Object actual) throws JsonProcessingException {
        OperationConfiguration configuration = operateConfigurationParser.parse(Person.class);
        System.out.println("before: " + objectMapper.writeValueAsString(actual));
        operateTemplate.process(actual, configuration);
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
        Assertions.assertEquals(objectMapper.writeValueAsString(expected), objectMapper.writeValueAsString(actual));
    }

    /**
     * 处理多个普通对象
     */
    @SneakyThrows
    @Test
    public void testMultiBeanByAnnotationConfig() {
        Person[] actual = new Person[]{getActualPerson(), getActualPerson()};
        processAndLog(actual);

        Assertions.assertEquals(
            objectMapper.writeValueAsString(Arrays.asList(getExpectedPerson(), getExpectedPerson())),
            objectMapper.writeValueAsString(Arrays.asList(actual))
        );
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
        Assertions.assertEquals(objectMapper.writeValueAsString(expected), objectMapper.writeValueAsString(actual));
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
        Assertions.assertEquals(objectMapper.writeValueAsString(Arrays.asList(expected)), objectMapper.writeValueAsString(Arrays.asList(actual)));
    }

}
