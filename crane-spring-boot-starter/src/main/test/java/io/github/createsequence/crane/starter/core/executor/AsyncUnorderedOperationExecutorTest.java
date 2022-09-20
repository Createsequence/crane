package io.github.createsequence.crane.starter.core.executor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import io.github.createsequence.crane.core.annotation.AssembleKV;
import io.github.createsequence.crane.core.annotation.Prop;
import io.github.createsequence.crane.core.cache.ConfigurationCache;
import io.github.createsequence.crane.core.container.BeanIntrospectContainer;
import io.github.createsequence.crane.core.container.Container;
import io.github.createsequence.crane.core.container.KeyIntrospectContainer;
import io.github.createsequence.crane.core.container.KeyValueContainer;
import io.github.createsequence.crane.core.executor.AsyncUnorderedOperationExecutor;
import io.github.createsequence.crane.core.helper.DefaultGroup;
import io.github.createsequence.crane.core.helper.reflex.ReflexUtils;
import io.github.createsequence.crane.core.operator.BeanReflexAssembler;
import io.github.createsequence.crane.core.operator.BeanReflexDisassembler;
import io.github.createsequence.crane.core.operator.BeanReflexOperateProcessor;
import io.github.createsequence.crane.core.operator.interfaces.Assembler;
import io.github.createsequence.crane.core.parser.*;
import io.github.createsequence.crane.core.parser.interfaces.AssembleOperation;
import io.github.createsequence.crane.core.parser.interfaces.DisassembleOperation;
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
import java.util.concurrent.Executors;
import java.util.function.Supplier;

/**
 * @author huangchengxing
 * @date 2022/06/01 17:12
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CoreTestConfig.class)
public class AsyncUnorderedOperationExecutorTest {

    @Autowired
    private BeanReflexOperateProcessor beanReflexOperateProcessor;
    @Autowired
    private GlobalConfiguration globalConfiguration;
    @Autowired
    FieldAnnotationConfigurationParser fieldAnnotationConfigurationParser;
    @Autowired
    ConfigurationCache ConfigurationCache;
    @Autowired
    KeyValueContainer keyValueContainer;

    @Test
    public void testAsyncUnorderedOperationExecutor() {
        Container keyIntrospectContainer = new KeyIntrospectContainer();
        Container beanIntrospectContainer = new BeanIntrospectContainer();
        keyValueContainer.register("test", MapUtil.builder().put(0, "动态的嵌套对象").build());

        // 获取配置
        Assembler assembler = new BeanReflexAssembler(beanReflexOperateProcessor);
        OperationConfiguration configuration = new BeanOperationConfiguration(globalConfiguration, Example.class, new ArrayList<>(), new ArrayList<>());
        AssembleOperation idIntrospectOperation = new BeanAssembleOperation(
            0, configuration, ReflexUtils.findField(Example.class, "id"),
            Collections.emptySet(), "", keyIntrospectContainer, assembler,
            Collections.singletonList(new BeanPropertyMapping("introspectId", "", "", Void.class)),
            Collections.singleton(DefaultGroup.class)
        );
        configuration.getAssembleOperations().add(idIntrospectOperation);
        AssembleOperation nameIntrospectOperation = new BeanAssembleOperation(
            0, configuration, ReflexUtils.findField(Example.class, "name"),
            Collections.emptySet(), "", beanIntrospectContainer, assembler,
            Collections.singletonList(new BeanPropertyMapping("introspectName", "name", "", Void.class)),
            Collections.singleton(DefaultGroup.class)
        );
        configuration.getAssembleOperations().add(nameIntrospectOperation);

        // 获取装卸配置
        DisassembleOperation disassembleOperation = new BeanDynamicDisassembleOperation(
            fieldAnnotationConfigurationParser, 0,
            configuration,
            new BeanReflexDisassembler(beanReflexOperateProcessor),
            ReflexUtils.findField(Example.class, "dynamicObject"),
            Collections.emptySet(),
            ConfigurationCache
        );
        configuration.getDisassembleOperations().add(disassembleOperation);

        // 执行操作
        Supplier<Example<?>> nestExample = () -> new Example<>(0, null, null);
        List<Example<Example<?>>> examples = Arrays.asList(new Example<>(1, "小明", nestExample), new Example<>(2, "小王", nestExample), new Example<>(3, "小李", nestExample));
        new AsyncUnorderedOperationExecutor(Executors.newFixedThreadPool(2)).execute(examples, configuration);

        Map<Integer, Example<Example<?>>> exampleMap = CollUtil.toMap(examples, new HashMap<>(4), Example::getId);
        Assertions.assertEquals(3, exampleMap.size());
        Assertions.assertEquals(1, exampleMap.get(1).getIntrospectId());
        Assertions.assertEquals(2, exampleMap.get(2).getIntrospectId());
        Assertions.assertEquals(3, exampleMap.get(3).getIntrospectId());
        Assertions.assertEquals("动态的嵌套对象", exampleMap.get(1).getDynamicObject().getName());
        Assertions.assertEquals("动态的嵌套对象", exampleMap.get(2).getDynamicObject().getName());
        Assertions.assertEquals("动态的嵌套对象", exampleMap.get(3).getDynamicObject().getName());
        Assertions.assertEquals("小明", exampleMap.get(1).getIntrospectName());
        Assertions.assertEquals("小王", exampleMap.get(2).getIntrospectName());
        Assertions.assertEquals("小李", exampleMap.get(3).getIntrospectName());

    }

    @Data
    private static class Example<T> {
        private String name;
        @AssembleKV(namespace = "test", props = @Prop(ref = "name"))
        private Integer id;
        private String introspectName;
        private Integer introspectId;
        private T dynamicObject;
        public Example(Integer id, String name, Supplier<T> supplier) {
            this.id = id;
            this.name = name;
            this.dynamicObject = Objects.isNull(supplier) ? null : supplier.get();
        }
    }

}
