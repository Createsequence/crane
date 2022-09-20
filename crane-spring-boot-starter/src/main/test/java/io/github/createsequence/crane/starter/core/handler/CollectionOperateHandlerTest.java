package io.github.createsequence.crane.starter.core.handler;

import cn.hutool.core.collection.CollStreamUtil;
import io.github.createsequence.crane.core.handler.BeanOperateHandler;
import io.github.createsequence.crane.core.handler.CollectionOperateHandler;
import io.github.createsequence.crane.core.handler.MapOperateHandler;
import io.github.createsequence.crane.core.handler.interfaces.OperateHandler;
import io.github.createsequence.crane.core.helper.DefaultGroup;
import io.github.createsequence.crane.core.helper.property.AsmReflexBeanPropertyFactory;
import io.github.createsequence.crane.core.helper.reflex.ReflexUtils;
import io.github.createsequence.crane.core.operator.BeanReflexOperateProcessor;
import io.github.createsequence.crane.core.parser.BeanAssembleOperation;
import io.github.createsequence.crane.core.parser.BeanPropertyMapping;
import io.github.createsequence.crane.core.parser.interfaces.AssembleOperation;
import io.github.createsequence.crane.core.parser.interfaces.PropertyMapping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author huangchengxing
 * @date 2022/06/01 19:45
 */
public class CollectionOperateHandlerTest {

    @Test
    public void testCollectionOperateHandler() {
        AssembleOperation assembleOperation = new BeanAssembleOperation(
            0, null, ReflexUtils.findField(Example.class, "id"),
            Collections.emptySet(), "", null, null,
            Collections.emptyList(), Collections.singleton(DefaultGroup.class)
        );

        // source.xxx -> target.xxx
        BeanReflexOperateProcessor beanReflexOperateProcessor = new BeanReflexOperateProcessor();
        beanReflexOperateProcessor
            .registerTargetWriters(new BeanOperateHandler(beanReflexOperateProcessor, new AsmReflexBeanPropertyFactory()))
            .registerSourceReaders(new BeanOperateHandler(beanReflexOperateProcessor, new AsmReflexBeanPropertyFactory()))
            .registerTargetWriters(new MapOperateHandler(beanReflexOperateProcessor))
            .registerSourceReaders(new MapOperateHandler(beanReflexOperateProcessor));
        OperateHandler handler = new CollectionOperateHandler(beanReflexOperateProcessor);
        List<Example> target = Arrays.asList(new Example(1, "小明", null, null), new Example(2, "小李", null, null));
        List<Example> source = Arrays.asList(new Example(3, "小红", null, null), new Example(4, "小刚", null, null));
        PropertyMapping targetPropertyAndSourceProperty = new BeanPropertyMapping("name", "name", "", Void.class);
        Assertions.assertTrue(handler.sourceCanRead(source, targetPropertyAndSourceProperty, assembleOperation));
        Assertions.assertEquals(Arrays.asList("小红", "小刚"), handler.readFromSource(source, targetPropertyAndSourceProperty, assembleOperation));
        Assertions.assertTrue(handler.targetCanWrite("小红", target, targetPropertyAndSourceProperty, assembleOperation));
        handler.writeToTarget( "小红", target, targetPropertyAndSourceProperty, assembleOperation);
        Assertions.assertEquals(Arrays.asList("小红", "小红"), CollStreamUtil.toList(target, Example::name));

        // source -> target.xxx
        target = Arrays.asList(new Example(1, "小明", null, null), new Example(2, "小李", null, null));
        PropertyMapping targetPropertyAndSource = new BeanPropertyMapping("example", "", "", Void.class);
        Assertions.assertTrue(handler.sourceCanRead(source, targetPropertyAndSource, assembleOperation));
        Assertions.assertEquals(source, handler.readFromSource(source, targetPropertyAndSource, assembleOperation));
        Assertions.assertTrue(handler.targetCanWrite(source, target, targetPropertyAndSource, assembleOperation));
        handler.writeToTarget(source, target, targetPropertyAndSource, assembleOperation);
        Assertions.assertEquals(Arrays.asList(source, source), CollStreamUtil.toList(target, Example::example));

        // source.xxx -> target
        assembleOperation = new BeanAssembleOperation(
            0, null, ReflexUtils.findField(Example.class, "names"),
            Collections.emptySet(), "", null, null,
            Collections.emptyList(), Collections.singleton(DefaultGroup.class)
        );
        target = Arrays.asList(new Example(1, "小明", null, null), new Example(2, "小李", null, null));
        PropertyMapping targetAndSourceProperty = new BeanPropertyMapping("", "name", "", Void.class);
        Assertions.assertTrue(handler.sourceCanRead(source, targetAndSourceProperty, assembleOperation));
        Assertions.assertEquals(Arrays.asList("小红", "小刚"), handler.readFromSource(source, targetAndSourceProperty, assembleOperation));
        Assertions.assertTrue(handler.targetCanWrite(Arrays.asList("小红", "小刚"), target, targetAndSourceProperty, assembleOperation));
        handler.writeToTarget(Arrays.asList("小红", "小刚"), target, targetAndSourceProperty, assembleOperation);
        Assertions.assertEquals(
            Arrays.asList(Arrays.asList("小红", "小刚"), Arrays.asList("小红", "小刚")),
            CollStreamUtil.toList(target, Example::names)
        );

        //// source -> target
        assembleOperation = new BeanAssembleOperation(
            0, null, ReflexUtils.findField(Example.class, "example"),
            Collections.emptySet(), "", null, null,
            Collections.emptyList(), Collections.singleton(DefaultGroup.class)
        );
        target = Arrays.asList(new Example(1, "小明", null, null), new Example(2, "小李", null, null));
        PropertyMapping targetAndSource = new BeanPropertyMapping("", "", "", Void.class);
        Assertions.assertTrue(handler.sourceCanRead(source, targetAndSource, assembleOperation));
        Assertions.assertEquals(source, handler.readFromSource(source, targetAndSource, assembleOperation));
        Assertions.assertTrue(handler.targetCanWrite(source, target, targetAndSource, assembleOperation));
        handler.writeToTarget(source, target, targetAndSource, assembleOperation);
        Assertions.assertEquals(Arrays.asList(source, source), CollStreamUtil.toList(target, Example::example));
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true, fluent = true)
    @Data
    private static class Example {
        private Integer id;
        private String name;
        private List<String> names;
        private List<Example> example;
    }

}
