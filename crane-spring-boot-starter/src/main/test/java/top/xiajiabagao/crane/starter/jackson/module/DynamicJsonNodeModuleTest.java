package top.xiajiabagao.crane.starter.jackson.module;

import cn.hutool.core.map.MapUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.xiajiabagao.crane.starter.jackson.JacksonTestConfig;
import top.xiajibagao.crane.core.annotation.Assemble;
import top.xiajibagao.crane.core.annotation.Disassemble;
import top.xiajibagao.crane.core.container.KeyValueContainer;
import top.xiajibagao.crane.core.parser.interfaces.GlobalConfiguration;
import top.xiajibagao.crane.jackson.impl.annotation.ProcessJacksonNode;
import top.xiajibagao.crane.jackson.impl.operator.JacksonAssembler;
import top.xiajibagao.crane.jackson.impl.operator.JacksonDisassembler;

import java.util.Arrays;
import java.util.List;

/**
 * @author huangchengxing
 * @date 2022/06/07 13:30
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JacksonTestConfig.class)
public class DynamicJsonNodeModuleTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private KeyValueContainer keyValueContainer;
    @Autowired
    private GlobalConfiguration globalConfiguration;
    @Autowired
    private BeanFactory beanFactory;

    @SneakyThrows
    @Test
    public void testDynamicJsonNodeModule() {
        keyValueContainer.register("sex", MapUtil.builder().put(1, "男").put(0, "女").build());
        String actual = objectMapper.writeValueAsString(
            new Example(1, 1, Arrays.asList(
                new Example(2, 1, null),
                new Example(3, 0, null)
            ))
        );

        JsonNodeFactory nodeFactory = objectMapper.getNodeFactory();
        JsonNode expected = nodeFactory.objectNode()
            .put("id", 1)
            .put("sex", "男")
            .set("examples", nodeFactory.arrayNode()
                .add(nodeFactory.objectNode().put("id", 2).put("sex", "男"))
                .add(nodeFactory.objectNode().put("id", 3).put("sex", "女"))
            );
        Assertions.assertEquals(expected, objectMapper.readTree(actual));
    }

    @ProcessJacksonNode
    @AllArgsConstructor
    @Data
    private static class Example {
        private Integer id;
        @Assemble(namespace = "sex", assembler = JacksonAssembler.class)
        private Integer sex;
        @Disassemble(value = Example.class, disassembler = JacksonDisassembler.class)
        List<Example> examples;
    }

}
