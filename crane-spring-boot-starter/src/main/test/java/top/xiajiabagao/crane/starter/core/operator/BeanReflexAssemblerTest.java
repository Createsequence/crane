package top.xiajiabagao.crane.starter.core.operator;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
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
import top.xiajibagao.crane.core.parser.BeanAssembleProperty;
import top.xiajibagao.crane.core.parser.BeanOperationConfiguration;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.GlobalConfiguration;
import top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration;

import java.util.*;

/**
 * @author huangchengxing
 * @date 2022/05/31 17:14
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExampleConfig.class)
public class BeanReflexAssemblerTest {

    @Autowired
    private BeanReflexOperateHandlerChain beanReflexOperateHandlerChain;
    @Autowired
    private KeyValueContainer keyValueContainer;
    @Autowired
    GlobalConfiguration globalConfiguration;
    @Autowired
    BeanFactory beanFactory;

    @Test
    public void testBeanReflexAssembler() {
        // 填充数据
        Map<String, Example> sourceBean = new HashMap<>();
        Example source = new Example(1, "小明", 16, "");
        sourceBean.put("1", source);
        keyValueContainer.register("source", sourceBean);

        // 获取配置
        Assembler assembler = new BeanReflexAssembler(beanReflexOperateHandlerChain);
        OperationConfiguration configuration = new BeanOperationConfiguration(globalConfiguration, Example.class, new ArrayList<>(), new ArrayList<>());
        AssembleOperation assembleOperation = new BeanAssembleOperation(
            0, configuration, ReflexUtils.findField(Example.class, "id"),
            Collections.emptySet(), "source", keyValueContainer, assembler,
            Arrays.asList(
                new BeanAssembleProperty("name", "name", "", Void.class),
                new BeanAssembleProperty("age", "age", "", Void.class),
                new BeanAssembleProperty("type", "age", "#source >= 18 ? '成年人' : '未成年人'", String.class)
            ),
            Collections.singleton(DefaultGroup.class)
        );
        configuration.getAssembleOperations().add(assembleOperation);

        // 获取key值
        Example target = new Example(1);
        Object key = assembler.getKey(target, assembleOperation);
        Assertions.assertEquals(1, key);

        // 填充数据
        assembler.execute(target, source, assembleOperation);
        Assertions.assertEquals("小明", target.getName());
        Assertions.assertEquals(16, target.getAge());
        Assertions.assertEquals("未成年人", target.getType());
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
