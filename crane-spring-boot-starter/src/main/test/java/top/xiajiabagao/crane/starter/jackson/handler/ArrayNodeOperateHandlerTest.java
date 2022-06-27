package top.xiajiabagao.crane.starter.jackson.handler;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.IterUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandler;
import top.xiajibagao.crane.core.helper.DefaultGroup;
import top.xiajibagao.crane.core.helper.reflex.ReflexUtils;
import top.xiajibagao.crane.core.parser.BeanAssembleOperation;
import top.xiajibagao.crane.core.parser.BeanPropertyMapping;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.PropertyMapping;
import top.xiajibagao.crane.jackson.impl.handler.ArrayNodeOperateHandler;
import top.xiajibagao.crane.jackson.impl.handler.ObjectNodeOperateHandler;
import top.xiajibagao.crane.jackson.impl.handler.ValueNodeOperateHandler;
import top.xiajibagao.crane.jackson.impl.operator.JacksonOperateProcessor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author huangchengxing
 * @date 2022/06/07 12:27
 */
public class ArrayNodeOperateHandlerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JacksonOperateProcessor jacksonOperateProcessor = new JacksonOperateProcessor();

    @Test
    public void testArrayNodeOperateHandler() {
        AssembleOperation assembleOperation = new BeanAssembleOperation(
            0, null, ReflexUtils.findField(Example.class, "id"),
            Collections.emptySet(), "", null, null,
            Collections.emptyList(), Collections.singleton(DefaultGroup.class)
        );

        // source.xxx -> target.xxx
        jacksonOperateProcessor
            .registerTargetWriters(new ValueNodeOperateHandler(objectMapper, jacksonOperateProcessor))
            .registerSourceReaders(new ValueNodeOperateHandler(objectMapper, jacksonOperateProcessor))
            .registerTargetWriters(new ObjectNodeOperateHandler(objectMapper, jacksonOperateProcessor))
            .registerSourceReaders(new ObjectNodeOperateHandler(objectMapper, jacksonOperateProcessor));
        OperateHandler handler = new ArrayNodeOperateHandler(objectMapper, jacksonOperateProcessor);
        JsonNode target = objectMapper.valueToTree(Arrays.asList(new Example(1, "小明", null, null), new Example(2, "小李", null, null)));
        JsonNode source = objectMapper.valueToTree(Arrays.asList(new Example(3, "小红", null, null), new Example(4, "小刚", null, null)));
        PropertyMapping targetPropertyAndSourceProperty = new BeanPropertyMapping("name", "name", "", Void.class);
        Assertions.assertTrue(handler.sourceCanRead(source, targetPropertyAndSourceProperty, assembleOperation));
        Assertions.assertEquals(objectMapper.valueToTree(Arrays.asList("小红", "小刚")), handler.readFromSource(source, targetPropertyAndSourceProperty, assembleOperation));
        Assertions.assertTrue(handler.targetCanWrite("小红", target, targetPropertyAndSourceProperty, assembleOperation));
        handler.writeToTarget( "小红", target, targetPropertyAndSourceProperty, assembleOperation);
        Assertions.assertEquals(Arrays.asList("小红", "小红"), CollStreamUtil.toList(IterUtil.toList(target), node -> node.get("name").textValue()));

        // source -> target.xxx
        target = objectMapper.valueToTree(Arrays.asList(new Example(1, "小明", null, null), new Example(2, "小李", null, null)));
        PropertyMapping targetPropertyAndSource = new BeanPropertyMapping("example", "", "", Void.class);
        Assertions.assertTrue(handler.sourceCanRead(source, targetPropertyAndSource, assembleOperation));
        Assertions.assertEquals(source, handler.readFromSource(source, targetPropertyAndSource, assembleOperation));
        Assertions.assertTrue(handler.targetCanWrite(source, target, targetPropertyAndSource, assembleOperation));
        handler.writeToTarget(source, target, targetPropertyAndSource, assembleOperation);
        Assertions.assertEquals(Arrays.asList(source, source), CollStreamUtil.toList(IterUtil.toList(target), node -> node.get("example")));

        // source.xxx -> target
        assembleOperation = new BeanAssembleOperation(
            0, null, ReflexUtils.findField(Example.class, "names"),
            Collections.emptySet(), "", null, null,
            Collections.emptyList(), Collections.singleton(DefaultGroup.class)
        );
        target = objectMapper.valueToTree(Arrays.asList(new Example(1, "小明", null, null), new Example(2, "小李", null, null)));
        PropertyMapping targetAndSourceProperty = new BeanPropertyMapping("", "name", "", Void.class);
        Assertions.assertTrue(handler.sourceCanRead(source, targetAndSourceProperty, assembleOperation));
        Assertions.assertEquals(objectMapper.valueToTree(Arrays.asList("小红", "小刚")), handler.readFromSource(source, targetAndSourceProperty, assembleOperation));
        Assertions.assertTrue(handler.targetCanWrite(Arrays.asList("小红", "小刚"), target, targetAndSourceProperty, assembleOperation));
        handler.writeToTarget(Arrays.asList("小红", "小刚"), target, targetAndSourceProperty, assembleOperation);
        Assertions.assertEquals(
            Arrays.asList(
                objectMapper.valueToTree(Arrays.asList("小红", "小刚")),
                objectMapper.valueToTree(Arrays.asList("小红", "小刚"))
            ),
            CollStreamUtil.toList(IterUtil.toList(target), node -> node.get("names"))
        );

        // source -> target
        assembleOperation = new BeanAssembleOperation(
            0, null, ReflexUtils.findField(Example.class, "example"),
            Collections.emptySet(), "", null, null,
            Collections.emptyList(), Collections.singleton(DefaultGroup.class)
        );
        target = objectMapper.valueToTree(Arrays.asList(new Example(1, "小明", null, null), new Example(2, "小李", null, null)));
        PropertyMapping targetAndSource = new BeanPropertyMapping("", "", "", Void.class);
        Assertions.assertTrue(handler.sourceCanRead(source, targetAndSource, assembleOperation));
        Assertions.assertEquals(source, handler.readFromSource(source, targetAndSource, assembleOperation));
        Assertions.assertTrue(handler.targetCanWrite(source, target, targetAndSource, assembleOperation));
        handler.writeToTarget(source, target, targetAndSource, assembleOperation);
        Assertions.assertEquals(Arrays.asList(source, source), CollStreamUtil.toList(IterUtil.toList(target), node -> node.get("example")));
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    @Data
    private static class Example {
        private Integer id;
        private String name;
        private List<String> names;
        private List<Example> example;
    }

}
