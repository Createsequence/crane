package top.xiajibagao.crane;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.xiajibagao.crane.config.TestService;
import top.xiajibagao.crane.container.EnumDictContainer;
import top.xiajibagao.crane.container.KeyValueContainer;
import top.xiajibagao.crane.model.BeanPerson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huangchengxing
 * @date 2022/03/23 23:16
 */
@SpringBootTest
public class ExtendFuncTests {

    @Autowired
    TestService testService;
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    EnumDictContainer enumDictContainer;
    @Autowired
    KeyValueContainer keyValueContainer;

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

}
