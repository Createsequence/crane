package io.github.createsequence.crane.starter.jackson.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ValueNode;
import io.github.createsequence.crane.core.handler.interfaces.OperateHandler;
import io.github.createsequence.crane.core.helper.DefaultGroup;
import io.github.createsequence.crane.core.helper.reflex.ReflexUtils;
import io.github.createsequence.crane.core.parser.BeanAssembleOperation;
import io.github.createsequence.crane.core.parser.BeanPropertyMapping;
import io.github.createsequence.crane.core.parser.interfaces.AssembleOperation;
import io.github.createsequence.crane.core.parser.interfaces.PropertyMapping;
import io.github.createsequence.crane.jackson.impl.handler.ValueNodeOperateHandler;
import io.github.createsequence.crane.jackson.impl.operator.JacksonOperateProcessor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Collections;

/**
 * @author huangchengxing
 * @date 2022/06/07 12:27
 */
public class ValueNodeOperateHandlerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testValueNodeOperateHandler() {
        AssembleOperation assembleOperation = new BeanAssembleOperation(
            0, null, ReflexUtils.findField(Example.class, "id"),
            Collections.emptySet(), "", null, null,
            Collections.emptyList(), Collections.singleton(DefaultGroup.class)
        );

        // source.xxx -> target.xxx
        OperateHandler handler = new ValueNodeOperateHandler(objectMapper, new JacksonOperateProcessor());
        JsonNode target = objectMapper.valueToTree("小明");
        JsonNode source = objectMapper.valueToTree("小红");
        Assertions.assertTrue(source instanceof ValueNode);
        PropertyMapping targetPropertyAndSourceProperty = new BeanPropertyMapping("name", "name", "", Void.class);
        Assertions.assertTrue(handler.sourceCanRead(source, targetPropertyAndSourceProperty, assembleOperation));
        Assertions.assertEquals(source, handler.readFromSource(source, targetPropertyAndSourceProperty, assembleOperation));
        Assertions.assertTrue(handler.targetCanWrite("小红", target, targetPropertyAndSourceProperty, assembleOperation));
        handler.writeToTarget( "小红", target, targetPropertyAndSourceProperty, assembleOperation);
        Assertions.assertEquals(objectMapper.valueToTree("小明"), target);

        // source -> target.xxx
        target = objectMapper.valueToTree("小明");
        PropertyMapping targetPropertyAndSource = new BeanPropertyMapping("example", "", "", Void.class);
        Assertions.assertTrue(handler.sourceCanRead(source, targetPropertyAndSource, assembleOperation));
        Assertions.assertEquals(source, handler.readFromSource(source, targetPropertyAndSource, assembleOperation));
        Assertions.assertTrue(handler.targetCanWrite("小红", target, targetPropertyAndSource, assembleOperation));
        handler.writeToTarget( "小红", target, targetPropertyAndSource, assembleOperation);
        Assertions.assertEquals(objectMapper.valueToTree("小明"), target);

        // source.xxx -> target
        assembleOperation = new BeanAssembleOperation(
            0, null, ReflexUtils.findField(Example.class, "name"),
            Collections.emptySet(), "", null, null,
            Collections.emptyList(), Collections.singleton(DefaultGroup.class)
        );
        target = objectMapper.valueToTree("小明");
        PropertyMapping targetAndSourceProperty = new BeanPropertyMapping("", "name", "", Void.class);
        Assertions.assertTrue(handler.sourceCanRead(source, targetAndSourceProperty, assembleOperation));
        Assertions.assertEquals(source, handler.readFromSource(source, targetAndSourceProperty, assembleOperation));
        Assertions.assertTrue(handler.targetCanWrite("小红", target, targetAndSourceProperty, assembleOperation));
        handler.writeToTarget( "小红", target, targetAndSourceProperty, assembleOperation);
        Assertions.assertEquals(objectMapper.valueToTree("小明"), target);


        // source -> target
        assembleOperation = new BeanAssembleOperation(
            0, null, ReflexUtils.findField(Example.class, "example"),
            Collections.emptySet(), "", null, null,
            Collections.emptyList(), Collections.singleton(DefaultGroup.class)
        );
        target = objectMapper.valueToTree("小明");
        PropertyMapping targetAndSource = new BeanPropertyMapping("", "", "", Void.class);
        Assertions.assertTrue(handler.sourceCanRead(source, targetAndSource, assembleOperation));
        Assertions.assertEquals(source, handler.readFromSource(source, targetAndSource, assembleOperation));
        Assertions.assertTrue(handler.targetCanWrite("小红", target, targetAndSource, assembleOperation));
        handler.writeToTarget("小红", target, targetAndSource, assembleOperation);
        Assertions.assertEquals(objectMapper.valueToTree("小明"), target);
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
