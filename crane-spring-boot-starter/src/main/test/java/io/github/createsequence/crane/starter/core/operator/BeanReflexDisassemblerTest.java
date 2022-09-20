package io.github.createsequence.crane.starter.core.operator;

import cn.hutool.core.collection.CollUtil;
import io.github.createsequence.crane.core.container.KeyValueContainer;
import io.github.createsequence.crane.core.helper.reflex.ReflexUtils;
import io.github.createsequence.crane.core.operator.BeanReflexDisassembler;
import io.github.createsequence.crane.core.operator.BeanReflexOperateProcessor;
import io.github.createsequence.crane.core.operator.interfaces.Disassembler;
import io.github.createsequence.crane.core.parser.BeanDisassembleOperation;
import io.github.createsequence.crane.core.parser.BeanOperationConfiguration;
import io.github.createsequence.crane.core.parser.interfaces.DisassembleOperation;
import io.github.createsequence.crane.core.parser.interfaces.GlobalConfiguration;
import io.github.createsequence.crane.core.parser.interfaces.OperationConfiguration;
import io.github.createsequence.crane.starter.core.CoreTestConfig;
import lombok.Data;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author huangchengxing
 * @date 2022/05/31 17:56
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CoreTestConfig.class)
public class BeanReflexDisassemblerTest {

    @Autowired
    private BeanReflexOperateProcessor beanReflexOperateProcessor;
    @Autowired
    private KeyValueContainer keyValueContainer;
    @Autowired
    GlobalConfiguration globalConfiguration;
    @Autowired
    BeanFactory beanFactory;

    @Test
    public void testBeanReflexDisassembler() {
        Disassembler disassembler = new BeanReflexDisassembler(beanReflexOperateProcessor);
        OperationConfiguration configuration = new BeanOperationConfiguration(globalConfiguration, Example.class, new ArrayList<>(), new ArrayList<>());
        DisassembleOperation disassembleOperation = new BeanDisassembleOperation(
            0, new BeanOperationConfiguration(globalConfiguration, Example.class, new ArrayList<>(), new ArrayList<>()),
            disassembler, configuration, ReflexUtils.findField(Example.class, "exampleList"), Collections.emptySet()
        );

        Example item = new Example();
        Example target = new Example();
        target.setExampleList(Arrays.asList(Arrays.asList(item)));
        Assertions.assertEquals(1, disassembler.execute(target, disassembleOperation).size());
        Object actual = CollUtil.getFirst(disassembler.execute(target, disassembleOperation));
        Assertions.assertEquals(item, actual);
    }

    @Data
    private static class Example {

        private List<List<Example>> exampleList;

    }

}
