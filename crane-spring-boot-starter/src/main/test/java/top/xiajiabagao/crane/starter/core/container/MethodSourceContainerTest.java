package top.xiajiabagao.crane.starter.core.container;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.xiajiabagao.crane.starter.core.CoreTestConfig;
import top.xiajibagao.crane.core.annotation.MappingType;
import top.xiajibagao.crane.core.annotation.MethodSourceBean;
import top.xiajibagao.crane.core.container.MethodSourceContainer;
import top.xiajibagao.crane.core.helper.DefaultGroup;
import top.xiajibagao.crane.core.helper.reflex.ReflexUtils;
import top.xiajibagao.crane.core.operator.BeanReflexAssembler;
import top.xiajibagao.crane.core.operator.BeanReflexOperateProcessor;
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
 * @date 2022/06/01 16:13
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CoreTestConfig.class, MethodSourceContainerTest.MethodSourceService.class})
public class MethodSourceContainerTest {

    @Autowired
    private BeanReflexOperateProcessor beanReflexOperateProcessor;
    @Autowired
    private GlobalConfiguration globalConfiguration;
    @Autowired
    MethodSourceService methodSourceService;
    @Autowired
    MethodSourceContainer methodSourceContainer;

    @Test
    public void testMethodSourceContainer() {
        // ???????????????
        Map<String, MethodSourceContainer.MethodSource> methodCache = methodSourceContainer.methodCache;
        MethodSourceContainer.MethodSource exampleMethodSource = methodCache.get("example");
        Assertions.assertNotNull(exampleMethodSource);
        Assertions.assertEquals(MappingType.ONE_TO_ONE, exampleMethodSource.getMappingType());
        MethodSourceContainer.MethodSource exampleGroupMethodSource = methodCache.get("exampleGroup");
        Assertions.assertNotNull(exampleGroupMethodSource);
        Assertions.assertEquals(MappingType.ONE_TO_MORE, exampleGroupMethodSource.getMappingType());

        // ????????????
        Assembler assembler = new BeanReflexAssembler(beanReflexOperateProcessor);
        OperationConfiguration configuration = new BeanOperationConfiguration(globalConfiguration, Example.class, new ArrayList<>(), new ArrayList<>());
        List<Example> examples = Arrays.asList(
            new Example(1, 1), new Example(2, 1),
            new Example(3, 2), new Example(4, 2)
        );

        // ???????????????
        AssembleOperation oneToOneOperation = new BeanAssembleOperation(
            0, configuration, ReflexUtils.findField(Example.class, "id"),
            Collections.emptySet(), "example", methodSourceContainer, assembler,
            Collections.singletonList(new BeanPropertyMapping("name", "name", "", Void.class)),
            Collections.singleton(DefaultGroup.class)
        );
        configuration.getAssembleOperations().add(oneToOneOperation);
        Multimap<AssembleOperation, Example> operations = ArrayListMultimap.create();
        examples.forEach(t -> operations.put(oneToOneOperation, t));
        methodSourceContainer.process(operations);
        Map<Integer, Example> exampleMap = CollUtil.toMap(examples, new HashMap<>(4), Example::getId);
        Assertions.assertEquals(4, exampleMap.size());
        Assertions.assertEquals("??????", Optional.ofNullable(exampleMap.get(1)).map(Example::getName).orElse(null));
        Assertions.assertEquals("??????", Optional.ofNullable(exampleMap.get(2)).map(Example::getName).orElse(null));
        Assertions.assertEquals("??????", Optional.ofNullable(exampleMap.get(3)).map(Example::getName).orElse(null));
        Assertions.assertEquals("??????", Optional.ofNullable(exampleMap.get(4)).map(Example::getName).orElse(null));

        // ???????????????
        AssembleOperation oneToMoreOperation = new BeanAssembleOperation(
            0, configuration, ReflexUtils.findField(Example.class, "groupId"),
            Collections.emptySet(), "exampleGroup", methodSourceContainer, assembler,
            Collections.singletonList(new BeanPropertyMapping("exampleList", "", "", Void.class)),
            Collections.singleton(DefaultGroup.class)
        );
        configuration.getAssembleOperations().add(oneToMoreOperation);
        Multimap<AssembleOperation, Example> oneToMoreOperations = ArrayListMultimap.create();
        examples.forEach(t -> oneToMoreOperations.put(oneToMoreOperation, t));
        methodSourceContainer.process(oneToMoreOperations);
        exampleMap = CollUtil.toMap(examples, new HashMap<>(4), Example::getId);
        List<Example> expectedGroup1 = Arrays.asList(
            new Example(1, "??????", 1, Collections.emptyList()),
            new Example(2, "??????", 1, Collections.emptyList())
        );
        List<Example> expectedGroup2 = Arrays.asList(
            new Example(3, "??????", 2, Collections.emptyList()),
            new Example(4, "??????", 2, Collections.emptyList())
        );
        Assertions.assertEquals(4, exampleMap.size());
        Assertions.assertEquals(expectedGroup1, Optional.ofNullable(exampleMap.get(1)).map(Example::getExampleList).orElse(null));
        Assertions.assertEquals(expectedGroup1, Optional.ofNullable(exampleMap.get(2)).map(Example::getExampleList).orElse(null));
        Assertions.assertEquals(expectedGroup2, Optional.ofNullable(exampleMap.get(3)).map(Example::getExampleList).orElse(null));
        Assertions.assertEquals(expectedGroup2, Optional.ofNullable(exampleMap.get(4)).map(Example::getExampleList).orElse(null));
    }

    @EqualsAndHashCode
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Example {
        private Integer id;
        private String name;
        private Integer groupId;
        private List<Example> exampleList;

        public Example(Integer id, Integer groupId) {
            this.id = id;
            this.groupId = groupId;
        }
    }

    /**
     * ????????????????????????????????????service
     */
    @MethodSourceBean
    protected static class MethodSourceService {

        @MethodSourceBean.Method (
            namespace = "example",
            sourceType = Example.class, sourceKey = "id",
            mappingType = MappingType.ONE_TO_ONE
        )
        public List<Example> listExampleByIds(Collection<Integer> ids) {
            return getExamples();
        }

        @MethodSourceBean.Method (
            namespace = "exampleGroup",
            sourceType = Example.class, sourceKey = "groupId",
            mappingType = MappingType.ONE_TO_MORE
        )
        public List<Example> listExampleByGroupIds() {
            return getExamples();
        }

        private static List<Example> getExamples() {
            return Arrays.asList(
                new Example(1, "??????", 1, Collections.emptyList()),
                new Example(2, "??????", 1, Collections.emptyList()),
                new Example(3, "??????", 2, Collections.emptyList()),
                new Example(4, "??????", 2, Collections.emptyList())
            );
        }
    }

}
