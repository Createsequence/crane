package top.xiajiabagao.crane.starter.core.handler;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import top.xiajibagao.crane.core.handler.ArrayOperateHandler;
import top.xiajibagao.crane.core.handler.BeanOperateHandler;
import top.xiajibagao.crane.core.handler.MapOperateHandler;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandler;
import top.xiajibagao.crane.core.helper.DefaultGroup;
import top.xiajibagao.crane.core.helper.property.AsmReflexBeanPropertyFactory;
import top.xiajibagao.crane.core.helper.reflex.ReflexUtils;
import top.xiajibagao.crane.core.operator.BeanReflexOperateProcessor;
import top.xiajibagao.crane.core.parser.BeanAssembleOperation;
import top.xiajibagao.crane.core.parser.BeanPropertyMapping;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.PropertyMapping;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author huangchengxing
 * @date 2022/06/01 19:59
 */
public class ArrayOperateHandlerTest {

    @Test
    public void testCollectionOperateHandler() {
        AssembleOperation assembleOperation = new BeanAssembleOperation(
            0, null, ReflexUtils.findField(Example.class, "id"),
            Collections.emptySet(), "", null, null,
            Collections.emptyList(), Collections.singleton(DefaultGroup.class)
        );

        // source.xxx -> target.xxx
        BeanReflexOperateProcessor operateProcessor = new BeanReflexOperateProcessor();
        operateProcessor.registerSourceReaders(new BeanOperateHandler(operateProcessor, new AsmReflexBeanPropertyFactory()))
            .registerTargetWriters(new BeanOperateHandler(operateProcessor, new AsmReflexBeanPropertyFactory()))
            .registerSourceReaders(new MapOperateHandler(operateProcessor))
            .registerTargetWriters(new MapOperateHandler(operateProcessor));
        OperateHandler handler = new ArrayOperateHandler(operateProcessor);
        Example[] target = new Example[] {new Example(1, "??????", null, null), new Example(2, "??????", null, null)};
        Example[] source = new Example[] {new Example(3, "??????", null, null), new Example(4, "??????", null, null)};
        PropertyMapping targetPropertyAndSourceProperty = new BeanPropertyMapping("name", "name", "", Void.class);
        Assertions.assertTrue(handler.sourceCanRead(source, targetPropertyAndSourceProperty, assembleOperation));
        Assertions.assertEquals(Arrays.asList("??????", "??????"), handler.readFromSource(source, targetPropertyAndSourceProperty, assembleOperation));
        Assertions.assertTrue(handler.targetCanWrite("??????", target, targetPropertyAndSourceProperty, assembleOperation));
        handler.writeToTarget( "??????", target, targetPropertyAndSourceProperty, assembleOperation);
        Assertions.assertEquals(Arrays.asList("??????", "??????"), CollStreamUtil.toList(Arrays.asList(target), Example::name));

        // source -> target.xxx
        target = new Example[] {new Example(1, "??????", null, null), new Example(2, "??????", null, null)};
        PropertyMapping targetPropertyAndSource = new BeanPropertyMapping("example", "", "", Void.class);
        Assertions.assertTrue(handler.sourceCanRead(source, targetPropertyAndSource, assembleOperation));
        Assertions.assertEquals(CollUtil.newArrayList(source), handler.readFromSource(source, targetPropertyAndSource, assembleOperation));
        Assertions.assertTrue(handler.targetCanWrite(CollUtil.newArrayList(source), target, targetPropertyAndSource, assembleOperation));
        handler.writeToTarget(CollUtil.newArrayList(source), target, targetPropertyAndSource, assembleOperation);
        Assertions.assertEquals(Arrays.asList(CollUtil.newArrayList(source), CollUtil.newArrayList(source)), CollStreamUtil.toList(Arrays.asList(target), Example::example));

        // source.xxx -> target
        assembleOperation = new BeanAssembleOperation(
            0, null, ReflexUtils.findField(Example.class, "names"),
            Collections.emptySet(), "", null, null,
            Collections.emptyList(), Collections.singleton(DefaultGroup.class)
        );
        target = new Example[] {new Example(1, "??????", null, null), new Example(2, "??????", null, null)};
        PropertyMapping targetAndSourceProperty = new BeanPropertyMapping("", "name", "", Void.class);
        Assertions.assertTrue(handler.sourceCanRead(source, targetAndSourceProperty, assembleOperation));
        Assertions.assertEquals(Arrays.asList("??????", "??????"), handler.readFromSource(source, targetAndSourceProperty, assembleOperation));
        Assertions.assertTrue(handler.targetCanWrite(Arrays.asList("??????", "??????"), target, targetAndSourceProperty, assembleOperation));
        handler.writeToTarget(Arrays.asList("??????", "??????"), target, targetAndSourceProperty, assembleOperation);
        Assertions.assertEquals(
            Arrays.asList(Arrays.asList("??????", "??????"), Arrays.asList("??????", "??????")),
            CollStreamUtil.toList(Arrays.asList(target), Example::names)
        );

        // source -> target
        assembleOperation = new BeanAssembleOperation(
            0, null, ReflexUtils.findField(Example.class, "example"),
            Collections.emptySet(), "", null, null,
            Collections.emptyList(), Collections.singleton(DefaultGroup.class)
        );
        target = new Example[] {new Example(1, "??????", null, null), new Example(2, "??????", null, null)};
        PropertyMapping targetAndSource = new BeanPropertyMapping("", "", "", Void.class);
        Assertions.assertTrue(handler.sourceCanRead(source, targetAndSource, assembleOperation));
        Assertions.assertEquals(CollUtil.newArrayList(source), handler.readFromSource(source, targetAndSource, assembleOperation));
        Assertions.assertTrue(handler.targetCanWrite(CollUtil.newArrayList(source), target, targetAndSource, assembleOperation));
        handler.writeToTarget(CollUtil.newArrayList(source), target, targetAndSource, assembleOperation);
        Assertions.assertEquals(Arrays.asList(CollUtil.newArrayList(source), CollUtil.newArrayList(source)), CollStreamUtil.toList(Arrays.asList(target), Example::example));
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
