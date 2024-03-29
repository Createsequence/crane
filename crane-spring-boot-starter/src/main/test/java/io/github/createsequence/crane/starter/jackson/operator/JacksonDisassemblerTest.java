package io.github.createsequence.crane.starter.jackson.operator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.createsequence.crane.core.container.KeyValueContainer;
import io.github.createsequence.crane.core.helper.reflex.ReflexUtils;
import io.github.createsequence.crane.core.operator.interfaces.Disassembler;
import io.github.createsequence.crane.core.parser.BeanDisassembleOperation;
import io.github.createsequence.crane.core.parser.BeanOperationConfiguration;
import io.github.createsequence.crane.core.parser.interfaces.DisassembleOperation;
import io.github.createsequence.crane.core.parser.interfaces.GlobalConfiguration;
import io.github.createsequence.crane.core.parser.interfaces.OperationConfiguration;
import io.github.createsequence.crane.jackson.impl.operator.JacksonDisassembler;
import io.github.createsequence.crane.jackson.impl.operator.JacksonOperateProcessor;
import io.github.createsequence.crane.starter.jackson.JacksonTestConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * @author huangchengxing
 * @date 2022/06/07 13:13
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JacksonTestConfig.class)
public class JacksonDisassemblerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private JacksonOperateProcessor jacksonOperateProcessor;
    @Autowired
    private KeyValueContainer keyValueContainer;
    @Autowired
    GlobalConfiguration globalConfiguration;
    @Autowired
    BeanFactory beanFactory;

    @SuppressWarnings("unchecked")
    @Test
    public void testJacksonDisassembler() {
        Disassembler disassembler = new JacksonDisassembler(objectMapper, jacksonOperateProcessor);
        OperationConfiguration configuration = new BeanOperationConfiguration(globalConfiguration, Example.class, new ArrayList<>(), new ArrayList<>());
        DisassembleOperation disassembleOperation = new BeanDisassembleOperation(
            0, new BeanOperationConfiguration(globalConfiguration, Example.class, new ArrayList<>(), new ArrayList<>()),
            disassembler, configuration, ReflexUtils.findField(Example.class, "exampleList"), Collections.emptySet()
        );

        Example item = new Example(Collections.emptyList());
        JsonNode target = objectMapper.valueToTree(new Example(Arrays.asList(Arrays.asList(item, item), Collections.singletonList(item))));
        Collection<JsonNode> nodes = (Collection<JsonNode>)disassembler.execute(target, disassembleOperation);
        Assertions.assertEquals(3, nodes.size());
        nodes.forEach(actual -> Assertions.assertEquals(objectMapper.valueToTree(item), actual));
    }

    @AllArgsConstructor
    @Data
    private static class Example {
        private List<List<Example>> exampleList;
    }

}
