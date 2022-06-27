package top.xiajiabagao.crane.starter.core.operator;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.xiajiabagao.crane.starter.core.CoreTestConfig;
import top.xiajibagao.crane.core.container.KeyValueContainer;
import top.xiajibagao.crane.core.helper.reflex.ReflexUtils;
import top.xiajibagao.crane.core.operator.BeanReflexDisassembler;
import top.xiajibagao.crane.core.operator.BeanReflexOperateProcessor;
import top.xiajibagao.crane.core.operator.interfaces.Disassembler;
import top.xiajibagao.crane.core.parser.BeanDisassembleOperation;
import top.xiajibagao.crane.core.parser.BeanOperationConfiguration;
import top.xiajibagao.crane.core.parser.interfaces.DisassembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.GlobalConfiguration;
import top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration;

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
