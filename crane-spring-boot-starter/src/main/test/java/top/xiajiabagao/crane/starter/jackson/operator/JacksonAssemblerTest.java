package top.xiajiabagao.crane.starter.jackson.operator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.xiajiabagao.crane.starter.jackson.JacksonTestConfig;
import top.xiajibagao.crane.core.container.KeyValueContainer;
import top.xiajibagao.crane.core.helper.DefaultGroup;
import top.xiajibagao.crane.core.helper.reflex.ReflexUtils;
import top.xiajibagao.crane.core.operator.interfaces.Assembler;
import top.xiajibagao.crane.core.parser.BeanAssembleOperation;
import top.xiajibagao.crane.core.parser.BeanOperationConfiguration;
import top.xiajibagao.crane.core.parser.BeanPropertyMapping;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.GlobalConfiguration;
import top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration;
import top.xiajibagao.crane.jackson.impl.operator.JacksonAssembler;
import top.xiajibagao.crane.jackson.impl.operator.JacksonOperateProcessor;

import java.util.*;

/**
 * @author huangchengxing
 * @date 2022/06/07 12:28
 */
@RequiredArgsConstructor
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JacksonTestConfig.class)
public class JacksonAssemblerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private JacksonOperateProcessor jacksonOperateProcessor;
    @Autowired
    private KeyValueContainer keyValueContainer;
    @Autowired
    GlobalConfiguration globalConfiguration;

    @Test
    public void testJacksonAssembler() {
        // 填充数据
        Map<String, Example> sourceBean = new HashMap<>();
        Example source = new Example(1, "小明", 16, "");
        sourceBean.put("1", source);
        keyValueContainer.register("source", sourceBean);

        // 获取配置
        Assembler assembler = new JacksonAssembler(objectMapper, jacksonOperateProcessor);
        OperationConfiguration configuration = new BeanOperationConfiguration(globalConfiguration, Example.class, new ArrayList<>(), new ArrayList<>());
        AssembleOperation assembleOperation = new BeanAssembleOperation(
            0, configuration, ReflexUtils.findField(Example.class, "id"),
            Collections.emptySet(), "source", keyValueContainer, assembler,
            Arrays.asList(
                new BeanPropertyMapping("name", "name", "", Void.class),
                new BeanPropertyMapping("age", "age", "", Void.class),
                new BeanPropertyMapping("type", "age", "#source.intValue() >= 18 ? '成年人' : '未成年人'", String.class)
            ),
            Collections.singleton(DefaultGroup.class)
        );
        configuration.getAssembleOperations().add(assembleOperation);

        // 获取key值
        JsonNode target = objectMapper.valueToTree(new Example(1));
        Object key = assembler.getKey(target, assembleOperation);
        Assertions.assertEquals("1", key);

        // 填充数据
        assembler.execute(target, source, assembleOperation);
        Assertions.assertEquals(objectMapper.valueToTree("小明"), target.get("name"));
        Assertions.assertEquals(objectMapper.valueToTree(16), target.get("age"));
        Assertions.assertEquals(objectMapper.valueToTree("未成年人"), target.get("type"));
    }

    @AllArgsConstructor
    @Data
    private static class Example {

        public Example(Integer id) {
            this.id = id;
        }

        private Integer id;
        private String name;
        private Integer age;
        private String type;
    }

}
