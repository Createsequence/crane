package top.xiajiabagao.crane.starter.extension;

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
import top.xiajiabagao.crane.starter.common.TestContainer;
import top.xiajibagao.crane.core.annotation.MappingType;
import top.xiajibagao.crane.core.annotation.MethodSourceBean;
import top.xiajibagao.crane.core.container.KeyValueContainer;
import top.xiajibagao.crane.core.aop.ProcessResult;

import java.util.*;

/**
 * 测试扩展功能：
 * 1、方法拦截切面能否正确处理方法单个/多个的嵌套/非嵌套返回值数据；
 * 2、方法拦截切面的条件表达式是否能正确生效；
 * 3、方法调用是否能正确从数据源容器根据命名空间获取数据源；
 *
 * @author huangchengxing
 * @date 2022/04/10 22:33
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfig.class, MethodResultProcessTest.TestService.class})
public class MethodResultProcessTest {

    @Qualifier("TestObjectMapper")
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TestService testService;

    // 容器
    @Autowired
    private KeyValueContainer keyValueContainer;
    @Autowired
    private TestContainer testContainer;

    @Before
    public void initDate() {
        // 初始化键值对容器
        Map<String, Object> gender = new HashMap<>();
        gender.put("0", "女");
        gender.put("1", "男");
        keyValueContainer.register("sex", gender);

        // 初始化自定义容器
        Map<String, Object> mockData = new HashMap<>();
        mockData.put("beanName", "小明");
        mockData.put("beanAge", 35);
        testContainer.getMockDatas()
            .put(1, mockData);
    }

    /**
     * 返回值为单个嵌套对象
     */
    @SneakyThrows
    @Test
    public void testSingleResultMethodAop() {
        Classroom expectedBefore = getActualClassroom(1);
        Classroom actualBefore = testService.getClassroom(false);
        System.out.println("before: " + objectMapper.writeValueAsString(actualBefore));
        Assertions.assertEquals(expectedBefore, actualBefore);

        Classroom expectedAfter = new Classroom()
            .setId(1)
            .setStudentNames(Arrays.asList("老王"))
            .setStudents(Arrays.asList(getActualMember(1).setName("老王")))
            .setTeachers(Arrays.asList(
                getActualMember(1).setName("小明").setSexName("男"),
                getActualMember(1).setName("小明").setSexName("男")
            ));
        Classroom actualAfter = testService.getClassroom(true);
        Assertions.assertEquals(expectedAfter, actualAfter);
        System.out.println("after: " + objectMapper.writeValueAsString(actualAfter));
    }

    /**
     * 返回值为多个嵌套对象
     */
    @SneakyThrows
    @Test
    public void testMultiResultsMethodAop() {
        List<Classroom> expectedBefore = Arrays.asList(getActualClassroom(1), getActualClassroom(2));
        List<Classroom> actualBefore = testService.listClassroom(false);
        System.out.println("before: " + objectMapper.writeValueAsString(testService.listClassroom(false)));
        Assertions.assertEquals(expectedBefore, actualBefore);

        List<Classroom> expectedAfter = Arrays.asList(
            getActualClassroom(1)
                .setStudents(Arrays.asList(getActualMember(1).setName("老王")))
                .setStudentNames(Arrays.asList("老王"))
                .setTeachers(Arrays.asList(
                    getActualMember(1).setName("小明").setSexName("男"),
                    getActualMember(1).setName("小明").setSexName("男")
                )),
            getActualClassroom(2)
                .setStudents(Arrays.asList(
                    getActualMember(2).setName("老李"),
                    getActualMember(2).setName("老白")
                ))
                .setStudentNames(Arrays.asList("老李", "老白"))
                .setTeachers(Arrays.asList(
                    getActualMember(2).setName("小明").setSexName("男"),
                    getActualMember(2).setName("小明").setSexName("男")
                ))
        );
        List<Classroom> actualAfter = testService.listClassroom(true);
        System.out.println("after: " + objectMapper.writeValueAsString(actualAfter));
        System.out.println("after: " + objectMapper.writeValueAsString(expectedAfter));
        Assertions.assertEquals(expectedAfter, actualAfter);
    }

    /**
     * @author huangchengxing
     * @date 2022/04/10 22:15
     */
    @MethodSourceBean
    public static class TestService {

        /**
         * 方法返回单个Classroom对象，isHandler为false时应当不对返回值做处理
         */
        @ProcessResult(targetClass = Classroom.class, condition = "#isHandler")
        public Classroom getClassroom(Boolean isHandler) {
            return getActualClassroom(1);
        }

        /**
         * 方法返回Classroom对象集合，isHandler为false时应当不对返回值做处理
         */
        @ProcessResult(targetClass = Classroom.class, condition = "#isHandler")
        public List<Classroom> listClassroom(Boolean isHandler) {
            return Arrays.asList(
                getClassroom(false).setId(1).setTeachers(Arrays.asList(getActualMember(1), getActualMember(1))),
                getClassroom(false).setId(2).setTeachers(Arrays.asList(getActualMember(2), getActualMember(2)))
            );
        }

        /**
         * 数据源方法，提供命名空间为student，返回值为Member，且分组id为classroomId, 与待处理对象关系为多对一的数据源
         */
        @MethodSourceBean.Method(namespace = "student", sourceType = Member.class, sourceKey = "classroomId", mappingType = MappingType.ONE_TO_MORE)
        public List<Member> listStudents(Set<Integer> classroomIds) {
            return Arrays.asList(
                getActualMember(1).setName("老王"),
                getActualMember(2).setName("老李"),
                getActualMember(2).setName("老白")
            );
        }
    }

    public static Classroom getActualClassroom(Integer classroomId) {
        return new Classroom()
            .setId(classroomId)
            .setTeachers(Arrays.asList(getActualMember(classroomId), getActualMember(classroomId)));
    }

    public static Member getActualMember(Integer classroomId) {
        return new Member()
            .setClassroomId(classroomId)
            .setId(1)
            .setSex(1);
    }
}
