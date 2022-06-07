package top.xiajiabagao.crane.starter.core.container;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.xiajiabagao.crane.starter.core.CoreTestConfig;
import top.xiajibagao.crane.core.container.EnumDictContainer;
import top.xiajibagao.crane.core.handler.BeanReflexOperateHandlerChain;
import top.xiajibagao.crane.core.helper.DefaultGroup;
import top.xiajibagao.crane.core.helper.EnumDict;
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
 * @date 2022/06/01 15:50
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CoreTestConfig.class)
public class EnumDictContainerTest {

    @Autowired
    private BeanReflexOperateHandlerChain beanReflexOperateHandlerChain;
    @Autowired
    private GlobalConfiguration globalConfiguration;

    @Test
    public void testEnumDictContainer() {
        // 注册数据源
        EnumDict enumDict = EnumDict.instance();
        EnumDictContainer enumDictContainer = new EnumDictContainer(enumDict);
        enumDictContainer.register(Gender.class);
        Assertions.assertEquals("sex", enumDict.getType(Gender.class).getName());
        Assertions.assertEquals(CollUtil.newHashSet("1", "0"), enumDict.getType(Gender.class).getNameCache().keySet());

        // 获取配置
        Assembler assembler = new BeanReflexAssembler(beanReflexOperateHandlerChain);
        OperationConfiguration configuration = new BeanOperationConfiguration(globalConfiguration, Example.class, new ArrayList<>(), new ArrayList<>());
        AssembleOperation assembleOperation = new BeanAssembleOperation(
            0, configuration, ReflexUtils.findField(Example.class, "sex"),
            Collections.emptySet(), "sex", enumDictContainer, assembler,
            Collections.singletonList(new BeanPropertyMapping("value", "desc", "", Void.class)),
            Collections.singleton(DefaultGroup.class)
        );
        configuration.getAssembleOperations().add(assembleOperation);

        // 执行操作
        List<Example> examples = Arrays.asList(
            new Example(1, 1), new Example(2, 0), new Example(3, 1)
        );
        Multimap<AssembleOperation, Example> operations = ArrayListMultimap.create();
        examples.forEach(t -> operations.put(assembleOperation, t));
        enumDictContainer.process(operations);

        Map<Integer, Example> exampleMap = CollUtil.toMap(examples, new HashMap<>(4), Example::getId);
        Assertions.assertEquals(3, exampleMap.size());
        Assertions.assertEquals(Gender.MALE.getDesc(), Optional.ofNullable(exampleMap.get(1)).map(Example::getValue).orElse(null));
        Assertions.assertEquals(Gender.FEMALE.getDesc(), Optional.ofNullable(exampleMap.get(2)).map(Example::getValue).orElse(null));
        Assertions.assertEquals(Gender.MALE.getDesc(), Optional.ofNullable(exampleMap.get(3)).map(Example::getValue).orElse(null));
    }

    @Data
    private static class Example {
        private Integer id;
        private Integer sex;
        private String value;

        public Example(Integer id, Integer sex) {
            this.id = id;
            this.sex = sex;
        }
    }

    @EnumDict.Item(typeName = "sex", itemNameProperty = "id")
    @Getter
    @RequiredArgsConstructor
    private enum Gender {
        MALE(1, "男"),
        FEMALE(0, "女");
        private final Integer id;
        private final String desc;
    }

}
