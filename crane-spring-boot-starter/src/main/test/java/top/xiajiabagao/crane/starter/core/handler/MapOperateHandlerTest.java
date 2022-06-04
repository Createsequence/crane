package top.xiajiabagao.crane.starter.core.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import top.xiajibagao.crane.core.handler.MapOperateHandler;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandler;
import top.xiajibagao.crane.core.helper.DefaultGroup;
import top.xiajibagao.crane.core.helper.reflex.ReflexUtils;
import top.xiajibagao.crane.core.parser.BeanAssembleOperation;
import top.xiajibagao.crane.core.parser.BeanPropertyMapping;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.PropertyMapping;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huangchengxing
 * @date 2022/06/01 17:56
 */
public class MapOperateHandlerTest {

    @Test
    public void testMapOperateHandler() {
        AssembleOperation assembleOperation = new BeanAssembleOperation(
            0, null, ReflexUtils.findField(Example.class, "id"),
            Collections.emptySet(), "", null, null,
            Collections.emptyList(), Collections.singleton(DefaultGroup.class)
        );

        // source.xxx -> target.xxx
        OperateHandler handler = new MapOperateHandler();
        Map<String, Object> target = new HashMap<>();
        target.put("id", 1);
        target.put("name", "小明");
        Map<String, Object> source = new HashMap<>();
        source.put("id", 2);
        source.put("name", "小红");
        PropertyMapping targetPropertyAndSourceProperty = new BeanPropertyMapping("name", "name", "", Void.class);
        Assertions.assertTrue(handler.sourceCanRead(source, targetPropertyAndSourceProperty, assembleOperation));
        Assertions.assertEquals("小红", handler.readFromSource(source, targetPropertyAndSourceProperty, assembleOperation));
        Assertions.assertTrue(handler.targetCanWrite("小红", target, targetPropertyAndSourceProperty, assembleOperation));
        handler.writeToTarget( "小红", target, targetPropertyAndSourceProperty, assembleOperation);
        Assertions.assertEquals("小红", target.get("name"));

        // source -> target.xxx
        target = new HashMap<>();
        target.put("id", 1);
        target.put("name", "小明");
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
        target = new HashMap<>();
        target.put("id", 1);
        target.put("name", "小明");
        PropertyMapping targetAndSourceProperty = new BeanPropertyMapping("", "name", "", Void.class);
        Assertions.assertTrue(handler.sourceCanRead(source, targetAndSourceProperty, assembleOperation));
        Assertions.assertEquals("小红", handler.readFromSource(source, targetAndSourceProperty, assembleOperation));
        Assertions.assertTrue(handler.targetCanWrite("小红", target, targetAndSourceProperty, assembleOperation));
        handler.writeToTarget("小红", target, targetAndSourceProperty, assembleOperation);
        Assertions.assertEquals("小红", target.get("name"));

        // source -> target
        assembleOperation = new BeanAssembleOperation(
            0, null, ReflexUtils.findField(Example.class, "example"),
            Collections.emptySet(), "", null, null,
            Collections.emptyList(), Collections.singleton(DefaultGroup.class)
        );
        target = new HashMap<>();
        target.put("id", 1);
        target.put("name", "小明");
        PropertyMapping targetAndSource = new BeanPropertyMapping("", "", "", Void.class);
        Assertions.assertTrue(handler.sourceCanRead(source, targetAndSource, assembleOperation));
        Assertions.assertEquals(source, handler.readFromSource(source, targetAndSource, assembleOperation));
        Assertions.assertTrue(handler.targetCanWrite(source, target, targetAndSource, assembleOperation));
        handler.writeToTarget(source, target, targetAndSource, assembleOperation);
        Assertions.assertEquals(source, target.get("example"));
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true, fluent = true)
    @Data
    private static class Example {
        private Integer id;
        private String name;
        private Example example;
    }

}
