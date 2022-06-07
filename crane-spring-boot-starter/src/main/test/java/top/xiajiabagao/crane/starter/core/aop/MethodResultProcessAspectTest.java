package top.xiajiabagao.crane.starter.core.aop;

import cn.hutool.core.collection.CollStreamUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;
import top.xiajiabagao.crane.starter.core.CoreTestConfig;
import top.xiajibagao.crane.core.annotation.Assemble;
import top.xiajibagao.crane.core.annotation.ProcessResult;
import top.xiajibagao.crane.core.annotation.Prop;
import top.xiajibagao.crane.core.container.KeyValueContainer;
import top.xiajibagao.crane.core.parser.ClassAnnotationConfigurationParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author huangchengxing
 * @date 2022/06/01 20:16
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CoreTestConfig.class, MethodResultProcessAspectTest.ExampleService.class})
public class MethodResultProcessAspectTest {

    @Autowired
    private ExampleService exampleService;
    @Autowired
    KeyValueContainer keyValueContainer;

    @Test
    public void testMethodResultProcessAspect() {
        // 注册数据源
        Map<Integer, Example> sources = new HashMap<>();
        sources.put(1, new Example(1, "小明", 15));
        sources.put(2, new Example(2, "小李", 20));
        sources.put(3, new Example(3, "小花", 25));
        keyValueContainer.register("test", sources);

        // 返回1~2，且填充数据
        List<Example> examples = exampleService.getExample(new QueryDTO(true, 2));
        Assertions.assertEquals(2, examples.size());
        Map<Integer, Example> exampleMap = CollStreamUtil.toMap(examples, Example::getId, Function.identity());
        Assertions.assertEquals(sources.get(1), exampleMap.get(1));
        Assertions.assertEquals(sources.get(2), exampleMap.get(2));

        // 返回1~2，且不填充数据
        examples = exampleService.getExample(new QueryDTO(false, 2));
        Assertions.assertEquals(2, examples.size());
        exampleMap = CollStreamUtil.toMap(examples, Example::getId, Function.identity());
        Assertions.assertNull(exampleMap.get(1).getName());
        Assertions.assertNull(exampleMap.get(2).getAge());
        Assertions.assertNull(exampleMap.get(1).getName());
        Assertions.assertNull(exampleMap.get(2).getAge());

        // 返回1，且不填充数据
        examples = exampleService.getExample(new QueryDTO(true, 1));
        Assertions.assertEquals(1, examples.size());
        exampleMap = CollStreamUtil.toMap(examples, Example::getId, Function.identity());
        Assertions.assertNull(exampleMap.get(1).getName());
        Assertions.assertNull(exampleMap.get(1).getName());

        // 返回2，且不填充数据
        examples = exampleService.getExampleProcessByClassAnnotation(new QueryDTO(true, 1));
        Assertions.assertEquals(1, examples.size());
        exampleMap = CollStreamUtil.toMap(examples, Example::getId, Function.identity());
        Assertions.assertNull(exampleMap.get(1).getName());
        Assertions.assertNull(exampleMap.get(1).getName());

        // 返回2，且不填充数据
        examples = exampleService.getExampleProcessByTestGroup(new QueryDTO(true, 1));
        Assertions.assertEquals(1, examples.size());
        exampleMap = CollStreamUtil.toMap(examples, Example::getId, Function.identity());
        Assertions.assertNull(exampleMap.get(1).getName());
        Assertions.assertNull(exampleMap.get(1).getName());
    }

    @Component
    public static class ExampleService {

        @ProcessResult(targetClass = Example.class, condition = "#queryDTO.resultCount > 1 && #queryDTO.enableOperate")
        public List<Example> getExample(QueryDTO queryDTO) {
            List<Example> results = new ArrayList<>();
            for (int i = 0; i < queryDTO.resultCount; i++) {
                results.add(new Example(i + 1));
            }
            return results;
        }

        // 指定使用类注解解析器
        @ProcessResult(
            targetClass = Example.class,
            parser = ClassAnnotationConfigurationParser.class
        )
        public List<Example> getExampleProcessByClassAnnotation(QueryDTO queryDTO) {
            List<Example> results = new ArrayList<>();
            for (int i = 0; i < queryDTO.resultCount; i++) {
                results.add(new Example(i + 1));
            }
            return results;
        }

        // 指定只填充TestGroup分组
        @ProcessResult(
            targetClass = Example.class,
            groups = TestGroup.class
        )
        public List<Example> getExampleProcessByTestGroup(QueryDTO queryDTO) {
            List<Example> results = new ArrayList<>();
            for (int i = 0; i < queryDTO.resultCount; i++) {
                results.add(new Example(i + 1));
            }
            return results;
        }


    }

    @AllArgsConstructor
    @Data
    private static class QueryDTO {
        private boolean enableOperate;
        private Integer resultCount;
    }

    @EqualsAndHashCode
    @AllArgsConstructor
    @Data
    private static class Example {
        @Assemble(namespace = "test", props = {
            @Prop(src = "name", ref = "name"),
            @Prop(src = "age", ref = "age")
        })
        private Integer id;
        private String name;
        private Integer age;

        public Example(Integer id) {
            this.id = id;
        }
    }

    private interface TestGroup {};

}
