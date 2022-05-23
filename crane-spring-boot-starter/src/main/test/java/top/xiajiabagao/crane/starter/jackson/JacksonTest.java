package top.xiajiabagao.crane.starter.jackson;

import cn.hutool.core.collection.CollStreamUtil;
import com.fasterxml.jackson.core.type.TypeReference;
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
import top.xiajiabagao.crane.starter.common.TestConfig;
import top.xiajibagao.crane.core.container.KeyValueContainer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试jackson实现能否在序列化的时候正确处理数据
 *
 * @author huangchengxing
 * @date 2022/04/17 19:20
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfig.class})
public class JacksonTest {

    @Qualifier("DefaultCraneJacksonSerializeObjectMapper")
    @Autowired
    private ObjectMapper serializeObjectMapper;

    @Qualifier("DefaultCraneJacksonObjectMapper")
    @Autowired
    private ObjectMapper objectMapper;

    // 容器
    @Autowired
    private KeyValueContainer keyValueContainer;

    @Before
    public void initDate() {
        // sex
        Map<String, Object> gender = new HashMap<>();
        gender.put("0", "女");
        gender.put("1", "男");
        keyValueContainer.register("sex", gender);

        // student
        Map<String, Object> students = new HashMap<>();
        students.put("1", Arrays.asList(
            getExpectedMember(1), getExpectedMember(1)
        ));
        keyValueContainer.register("student", students);

        // member
        Map<String, Object> members = new HashMap<>();
        members.put("1", getExpectedMember(1));
        keyValueContainer.register("member", members);
    }

    static Member getExpectedMember(Integer classroomId) {
        return new Member()
            .setClassroomId(classroomId)
            .setId(1)
            .setName("小明")
            .setSex(1)
            .setSexName("男");
    }

    static Classroom getExpectedClassroom(Integer classroomId) {
        return getActualClassroom(classroomId)
            .setStudentNames(CollStreamUtil.toList(
                Arrays.asList(getExpectedMember(classroomId), getExpectedMember(classroomId)),
                Member::getName
            ))
            .setStudents(Arrays.asList(
                getExpectedMember(classroomId), getExpectedMember(classroomId)
            ))
            .setMembers(Arrays.asList(
                getExpectedMember(classroomId), getExpectedMember(classroomId)
            ));
    }

    static Member getActualMember(Integer classroomId) {
        return new Member()
            .setSex(1)
            .setClassroomId(classroomId)
            .setId(1);
    }

    static Classroom getActualClassroom(Integer classroomId) {
        return new Classroom()
            .setName("classroom" + classroomId)
            .setId(classroomId)
            .setMembers(Arrays.asList(
                getActualMember(classroomId), getActualMember(classroomId)
            ));
    }

    /**
     * 测试单个对象的序列化处理
     */
    @SneakyThrows
    @Test
    public void singleNestBeanTest() {
        Classroom actualClassroom = getActualClassroom(1);
        System.out.println("before: " + objectMapper.writeValueAsString(actualClassroom));
        System.out.println("after: " + serializeObjectMapper.writeValueAsString(actualClassroom));

        Classroom exceptedClassroom = getExpectedClassroom(1);
        Assertions.assertEquals(
            exceptedClassroom,
            serializeObjectMapper.readValue(serializeObjectMapper.writeValueAsString(actualClassroom), Classroom.class)
        );
    }

    /**
     * 测试多个对象的序列化处理
     */
    @SneakyThrows
    @Test
    public void multiNestBeanTest() {
        List<Classroom> actualClassroom = Arrays.asList(getActualClassroom(1), getActualClassroom(1));
        System.out.println("before: " + objectMapper.writeValueAsString(actualClassroom));
        System.out.println("after: " + serializeObjectMapper.writeValueAsString(actualClassroom));

        List<Classroom> exceptedClassroom = Arrays.asList(getExpectedClassroom(1), getExpectedClassroom(1));
        Assertions.assertEquals(
            exceptedClassroom,
            serializeObjectMapper.readValue(serializeObjectMapper.writeValueAsString(actualClassroom), new TypeReference<List<Classroom>>() {})
        );
    }

}
