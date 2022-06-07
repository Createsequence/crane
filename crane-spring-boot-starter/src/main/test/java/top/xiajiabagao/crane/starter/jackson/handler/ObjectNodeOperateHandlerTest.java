package top.xiajiabagao.crane.starter.jackson.handler;

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
import top.xiajibagao.crane.jackson.impl.handler.ObjectNodeOperateHandler;
import top.xiajibagao.crane.jackson.impl.helper.JacksonUtils;

import java.util.Collections;

/**
 * @author huangchengxing
 * @date 2022/06/07 12:27
 */
public class ObjectNodeOperateHandlerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testObjectNodeOperateHandler() {
        AssembleOperation assembleOperation = new BeanAssembleOperation(
            0, null, ReflexUtils.findField(Example.class, "id"),
            Collections.emptySet(), "", null, null,
            Collections.emptyList(), Collections.singleton(DefaultGroup.class)
        );

        // source.xxx -> target.xxx
        OperateHandler handler = new ObjectNodeOperateHandler(objectMapper);
        JsonNode target = objectMapper.valueToTree(new Example(1, "小明", null));
        JsonNode source = objectMapper.valueToTree(new Example(2, "小红", null));
        PropertyMapping targetPropertyAndSourceProperty = new BeanPropertyMapping("name", "name", "", Void.class);
        Assertions.assertTrue(handler.sourceCanRead(source, targetPropertyAndSourceProperty, assembleOperation));
        Assertions.assertEquals(objectMapper.valueToTree("小红"), handler.readFromSource(source, targetPropertyAndSourceProperty, assembleOperation));
        Assertions.assertTrue(handler.targetCanWrite("小红", target, targetPropertyAndSourceProperty, assembleOperation));
        handler.writeToTarget( "小红", target, targetPropertyAndSourceProperty, assembleOperation);
        Assertions.assertEquals(objectMapper.valueToTree("小红"), target.get("name"));

        // source -> target.xxx
        target = objectMapper.valueToTree(new Example(1, "小明", null));
        PropertyMapping targetPropertyAndSource = new BeanPropertyMapping("example", "", "", Void.class);
        Assertions.assertTrue(handler.sourceCanRead(source, targetPropertyAndSource, assembleOperation));
        Assertions.assertEquals(source, handler.readFromSource(source, targetPropertyAndSource, assembleOperation));
        Assertions.assertTrue(handler.targetCanWrite(source, target, targetPropertyAndSource, assembleOperation));
        handler.writeToTarget(source, target, targetPropertyAndSource, assembleOperation);
        Assertions.assertEquals(source, target.get("example"));

        // source.xxx -> target
        assembleOperation = new BeanAssembleOperation(
            0, null, ReflexUtils.findField(Example.class, "name"),
            Collections.emptySet(), "", null, null,
            Collections.emptyList(), Collections.singleton(DefaultGroup.class)
        );
        target = objectMapper.valueToTree(new Example(1, "小明", null));
        PropertyMapping targetAndSourceProperty = new BeanPropertyMapping("", "name", "", Void.class);
        Assertions.assertTrue(handler.sourceCanRead(source, targetAndSourceProperty, assembleOperation));
        Assertions.assertEquals(objectMapper.valueToTree("小红"), handler.readFromSource(source, targetAndSourceProperty, assembleOperation));
        Assertions.assertTrue(handler.targetCanWrite("小红", target, targetAndSourceProperty, assembleOperation));
        handler.writeToTarget("小红", target, targetAndSourceProperty, assembleOperation);
        Assertions.assertEquals(JacksonUtils.valueToTree(objectMapper, "小红"), target.get("name"));

        // source -> target
        assembleOperation = new BeanAssembleOperation(
            0, null, ReflexUtils.findField(Example.class, "example"),
            Collections.emptySet(), "", null, null,
            Collections.emptyList(), Collections.singleton(DefaultGroup.class)
        );
        target = objectMapper.valueToTree(new Example(1, "小明", null));
        PropertyMapping targetAndSource = new BeanPropertyMapping("", "", "", Void.class);
        Assertions.assertTrue(handler.sourceCanRead(source, targetAndSource, assembleOperation));
        Assertions.assertEquals(source, handler.readFromSource(source, targetAndSource, assembleOperation));
        Assertions.assertTrue(handler.targetCanWrite(source, target, targetAndSource, assembleOperation));
        handler.writeToTarget(source, target, targetAndSource, assembleOperation);
        Assertions.assertEquals(source, target.get("example"));
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    @Data
    private static class Example {
        private Integer id;
        private String name;
        private Example example;
    }

}
