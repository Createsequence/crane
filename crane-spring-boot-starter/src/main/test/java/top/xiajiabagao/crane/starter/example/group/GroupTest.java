package top.xiajiabagao.crane.starter.example.group;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.xiajiabagao.crane.starter.example.common.Gender;
import top.xiajiabagao.crane.starter.example.common.TestConfig;
import top.xiajiabagao.crane.starter.example.common.TestContainer;
import top.xiajibagao.crane.core.container.EnumDictContainer;
import top.xiajibagao.crane.core.container.KeyValueContainer;
import top.xiajibagao.crane.core.helper.OperateTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试是否能够按照分组正确的选择性填充对象
 *
 * @author huangchengxing
 * @date 2022/05/19 8:49
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class GroupTest {

    @Autowired
    private ObjectMapper objectMapper;

    // 容器
    @Autowired
    private EnumDictContainer enumDictContainer;
    @Autowired
    private KeyValueContainer keyValueContainer;
    @Autowired
    private TestContainer testContainer;

    @Autowired
    private OperateTemplate operateTemplate;

    private static Member getActualMember() {
        return new Member()
            .setId(1)
            .setSex(1);
    }

    private static Member getExpectedUserGroupMember() {
        return new Member()
            .setId(1)
            .setName("小明")
            .setAge(35)
            .setSex(1);
    }

    private static Member getExpectedDefaultGroupMember() {
        return new Member()
            .setId(1)
            .setSex(1)
            .setSexName("男");
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

    @SneakyThrows
    @Test
    public void testSingleGroupOperate() {
        // 只填充userGroup
        Member actual1 = getActualMember();
        operateTemplate.process(actual1, UserGroup.class);
        System.out.println(objectMapper.writeValueAsString(actual1));
        Member expected1 = getExpectedUserGroupMember();
        Assertions.assertEquals(objectMapper.writeValueAsString(actual1), objectMapper.writeValueAsString(expected1));

        // 只填充defaultGroup
        Member actual2 = getActualMember();
        operateTemplate.process(actual2);
        System.out.println(objectMapper.writeValueAsString(actual2));
        Member expected2 = getExpectedDefaultGroupMember();
        Assertions.assertEquals(objectMapper.writeValueAsString(actual2), objectMapper.writeValueAsString(expected2));
    }

    @SneakyThrows
    @Test
    public void testMultiGroupOperate() {
        // 只填充userGroup
        List<Member> actual1 = Arrays.asList(getActualMember(), getActualMember(), getActualMember());
        operateTemplate.process(actual1, UserGroup.class);
        System.out.println(objectMapper.writeValueAsString(actual1));
        List<Member> expected1 = Arrays.asList(getExpectedUserGroupMember(), getExpectedUserGroupMember(), getExpectedUserGroupMember());
        Assertions.assertEquals(objectMapper.writeValueAsString(actual1), objectMapper.writeValueAsString(expected1));

        // 只填充defaultGroup
        List<Member> actual2 = Arrays.asList(getActualMember(), getActualMember(), getActualMember());
        operateTemplate.process(actual2);
        System.out.println(objectMapper.writeValueAsString(actual2));
        List<Member> expected2 = Arrays.asList(getExpectedDefaultGroupMember(), getExpectedDefaultGroupMember(), getExpectedDefaultGroupMember());
        Assertions.assertEquals(objectMapper.writeValueAsString(actual2), objectMapper.writeValueAsString(expected2));
    }

    @SneakyThrows
    @Test
    public void testSingleNestGroupOperate() {
        // 只填充userGroup
        Member actual1 = getActualMember()
            .setRelatives(Arrays.asList(getActualMember(), getActualMember()));
        operateTemplate.process(actual1, UserGroup.class);
        System.out.println(objectMapper.writeValueAsString(actual1));
        Member expected1 = getExpectedUserGroupMember()
            .setRelatives(Arrays.asList(getExpectedUserGroupMember(), getExpectedUserGroupMember()));
        Assertions.assertEquals(objectMapper.writeValueAsString(actual1), objectMapper.writeValueAsString(expected1));

        // 只填充defaultGroup
        Member actual2 = getActualMember()
            .setRelatives(Arrays.asList(getActualMember(), getActualMember()));
        operateTemplate.process(actual2);
        System.out.println(objectMapper.writeValueAsString(actual2));
        Member expected2 = getExpectedDefaultGroupMember()
            .setRelatives(Arrays.asList(getExpectedDefaultGroupMember(), getExpectedDefaultGroupMember()));
        Assertions.assertEquals(objectMapper.writeValueAsString(actual2), objectMapper.writeValueAsString(expected2));
    }

    @SneakyThrows
    @Test
    public void testMultiNestGroupOperate() {
        // 只填充userGroup
        List<Member> actual1 = Arrays.asList(
            getActualMember().setRelatives(Arrays.asList(getActualMember(), getActualMember())),
            getActualMember().setRelatives(Arrays.asList(getActualMember(), getActualMember()))
        );
        operateTemplate.process(actual1, UserGroup.class);
        System.out.println(objectMapper.writeValueAsString(actual1));
        List<Member> expected1 = Arrays.asList(
            getExpectedUserGroupMember().setRelatives(Arrays.asList(getExpectedUserGroupMember(), getExpectedUserGroupMember())),
            getExpectedUserGroupMember().setRelatives(Arrays.asList(getExpectedUserGroupMember(), getExpectedUserGroupMember()))
        );
        Assertions.assertEquals(objectMapper.writeValueAsString(actual1), objectMapper.writeValueAsString(expected1));

        // 只填充defaultGroup
        List<Member> actual2 = Arrays.asList(
            getActualMember().setRelatives(Arrays.asList(getActualMember(), getActualMember())),
            getActualMember().setRelatives(Arrays.asList(getActualMember(), getActualMember()))
        );
        operateTemplate.process(actual2);
        System.out.println(objectMapper.writeValueAsString(actual2));
        List<Member> expected2 = Arrays.asList(
            getExpectedDefaultGroupMember().setRelatives(Arrays.asList(getExpectedDefaultGroupMember(), getExpectedDefaultGroupMember())),
            getExpectedDefaultGroupMember().setRelatives(Arrays.asList(getExpectedDefaultGroupMember(), getExpectedDefaultGroupMember()))
        );
        Assertions.assertEquals(objectMapper.writeValueAsString(actual2), objectMapper.writeValueAsString(expected2));
    }

}
