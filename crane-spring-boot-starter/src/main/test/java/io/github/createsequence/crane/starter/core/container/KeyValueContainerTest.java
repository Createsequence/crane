package io.github.createsequence.crane.starter.core.container;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.github.createsequence.crane.core.container.KeyValueContainer;
import io.github.createsequence.crane.core.helper.DefaultGroup;
import io.github.createsequence.crane.core.helper.reflex.ReflexUtils;
import io.github.createsequence.crane.core.operator.BeanReflexAssembler;
import io.github.createsequence.crane.core.operator.BeanReflexOperateProcessor;
import io.github.createsequence.crane.core.operator.interfaces.Assembler;
import io.github.createsequence.crane.core.parser.BeanAssembleOperation;
import io.github.createsequence.crane.core.parser.BeanOperationConfiguration;
import io.github.createsequence.crane.core.parser.BeanPropertyMapping;
import io.github.createsequence.crane.core.parser.interfaces.AssembleOperation;
import io.github.createsequence.crane.core.parser.interfaces.GlobalConfiguration;
import io.github.createsequence.crane.core.parser.interfaces.OperationConfiguration;
import io.github.createsequence.crane.starter.core.CoreTestConfig;
import lombok.Data;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * @author huangchengxing
 * @date 2022/06/01 11:21
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CoreTestConfig.class)
public class KeyValueContainerTest {

    @Autowired
    private BeanReflexOperateProcessor beanReflexOperateProcessor;
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
        Assembler assembler = new BeanReflexAssembler(beanReflexOperateProcessor);
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
