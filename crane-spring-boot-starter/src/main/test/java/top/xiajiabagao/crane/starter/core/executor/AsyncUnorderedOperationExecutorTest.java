package top.xiajiabagao.crane.starter.core.executor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import lombok.Data;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.xiajiabagao.crane.starter.core.CoreTestConfig;
import top.xiajibagao.crane.core.annotation.AssembleKV;
import top.xiajibagao.crane.core.annotation.Prop;
import top.xiajibagao.crane.core.cache.ConfigurationCache;
import top.xiajibagao.crane.core.container.BeanIntrospectContainer;
import top.xiajibagao.crane.core.container.Container;
import top.xiajibagao.crane.core.container.KeyIntrospectContainer;
import top.xiajibagao.crane.core.container.KeyValueContainer;
import top.xiajibagao.crane.core.executor.AsyncUnorderedOperationExecutor;
import top.xiajibagao.crane.core.handler.BeanReflexOperateHandlerChain;
import top.xiajibagao.crane.core.helper.DefaultGroup;
import top.xiajibagao.crane.core.helper.reflex.ReflexUtils;
import top.xiajibagao.crane.core.operator.BeanReflexAssembler;
import top.xiajibagao.crane.core.operator.BeanReflexDisassembler;
import top.xiajibagao.crane.core.operator.interfaces.Assembler;
import top.xiajibagao.crane.core.parser.*;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.DisassembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.GlobalConfiguration;
import top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration;

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
    private BeanReflexOperateHandlerChain beanReflexOperateHandlerChain;
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
        Assembler assembler = new BeanReflexAssembler(beanReflexOperateHandlerChain);
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
            new BeanReflexDisassembler(beanReflexOperateHandlerChain),
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
