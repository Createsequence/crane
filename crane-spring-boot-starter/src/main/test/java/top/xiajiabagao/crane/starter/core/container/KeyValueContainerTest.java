package top.xiajiabagao.crane.starter.core.container;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Data;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.xiajiabagao.crane.starter.core.ExampleConfig;
import top.xiajibagao.crane.core.container.KeyValueContainer;
import top.xiajibagao.crane.core.handler.BeanReflexOperateHandlerChain;
import top.xiajibagao.crane.core.helper.DefaultGroup;
import top.xiajibagao.crane.core.helper.reflex.ReflexUtils;
import top.xiajibagao.crane.core.operator.BeanReflexAssembler;
import top.xiajibagao.crane.core.operator.interfaces.Assembler;
import top.xiajibagao.crane.core.parser.BeanAssembleOperation;
import top.xiajibagao.crane.core.parser.BeanOperationConfiguration;
import top.xiajibagao.crane.core.parser.BeanPropertyMapping;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.GlobalConfiguration;
import top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration;

import java.util.*;

/**
 * @author huangchengxing
 * @date 2022/06/01 11:21
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExampleConfig.class)
public class KeyValueContainerTest {

    @Autowired
    private BeanReflexOperateHandlerChain beanReflexOperateHandlerChain;
    @Autowired
    private GlobalConfiguration globalConfiguration;

    @Test
    public void testKeyValueContainer() {
        // 注册数据
        KeyValueContainer keyValueContainer = new KeyValueContainer();
        Map<Object, String> data = new HashMap<>();
        data.put("1", "id是1");
        data.put("2", "id是2");
        data.put("3", "id是3");
        keyValueContainer.register("test", data);

        // 获取配置
        Assembler assembler = new BeanReflexAssembler(beanReflexOperateHandlerChain);
        OperationConfiguration configuration = new BeanOperationConfiguration(globalConfiguration, Example.class, new ArrayList<>(), new ArrayList<>());
        AssembleOperation assembleOperation = new BeanAssembleOperation(
            0, configuration, ReflexUtils.findField(Example.class, "id"),
            Collections.emptySet(), "test", keyValueContainer, assembler,
            Collections.singletonList(new BeanPropertyMapping("value", "", "", Void.class)),
            Collections.singleton(DefaultGroup.class)
        );
        configuration.getAssembleOperations().add(assembleOperation);

        // 执行操作
        List<Example> examples = Arrays.asList(
            new Example(1), new Example(2), new Example(3)
        );
        Multimap<AssembleOperation, Example> operations = ArrayListMultimap.create();
        examples.forEach(t -> operations.put(assembleOperation, t));
        keyValueContainer.process(operations);

        Map<Integer, Example> exampleMap = CollUtil.toMap(examples, new HashMap<>(4), Example::getId);
        Assertions.assertEquals(3, exampleMap.size());
        Assertions.assertEquals("id是1", Optional.ofNullable(exampleMap.get(1)).map(Example::getValue).orElse(null));
        Assertions.assertEquals("id是2", Optional.ofNullable(exampleMap.get(2)).map(Example::getValue).orElse(null));
        Assertions.assertEquals("id是3", Optional.ofNullable(exampleMap.get(3)).map(Example::getValue).orElse(null));
    }

    @Data
    private static class Example {
        private Integer id;
        private String value;

        public Example(Integer id) {
            this.id = id;
        }
    }

}
