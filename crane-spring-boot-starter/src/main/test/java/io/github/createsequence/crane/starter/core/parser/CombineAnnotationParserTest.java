package io.github.createsequence.crane.starter.core.parser;

import io.github.createsequence.crane.core.annotation.Assemble;
import io.github.createsequence.crane.core.annotation.Disassemble;
import io.github.createsequence.crane.core.annotation.Operations;
import io.github.createsequence.crane.core.annotation.Prop;
import io.github.createsequence.crane.core.container.KeyValueContainer;
import io.github.createsequence.crane.core.container.MethodSourceContainer;
import io.github.createsequence.crane.core.helper.DefaultGroup;
import io.github.createsequence.crane.core.operator.BeanReflexAssembler;
import io.github.createsequence.crane.core.operator.BeanReflexDisassembler;
import io.github.createsequence.crane.core.parser.ClassAnnotationConfigurationParser;
import io.github.createsequence.crane.core.parser.CombineOperationConfigurationParser;
import io.github.createsequence.crane.core.parser.FieldAnnotationConfigurationParser;
import io.github.createsequence.crane.core.parser.interfaces.*;
import io.github.createsequence.crane.starter.core.CoreTestConfig;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;

/**
 * @author huangchengxing
 * @date 2022/05/30 15:11
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CoreTestConfig.class)
public class CombineAnnotationParserTest {

    @Autowired
    private KeyValueContainer keyValueContainer;
    @Autowired
    private BeanReflexAssembler beanReflexAssembler;
    @Autowired
    private MethodSourceContainer methodSourceContainer;
    @Autowired
    private BeanReflexDisassembler beanReflexDisassembler;

    @Autowired
    GlobalConfiguration globalConfiguration;
    @Autowired
    BeanFactory beanFactory;

    @Test
    public void testParseFieldAndClassAnnotation() {
        OperateConfigurationParser fieldAnnotationConfigurationParser = new FieldAnnotationConfigurationParser(globalConfiguration, beanFactory);
        OperateConfigurationParser classAnnotationConfigurationParser = new ClassAnnotationConfigurationParser(globalConfiguration, beanFactory);
        OperateConfigurationParser combineParser = new CombineOperationConfigurationParser()
            .addParser(fieldAnnotationConfigurationParser)
            .addParser(classAnnotationConfigurationParser);

        OperationConfiguration configuration = combineParser.parse(Example.class);
        // 装配操作
        Assertions.assertEquals(Example.class, configuration.getTargetClass());
        Assertions.assertEquals(globalConfiguration, configuration.getGlobalConfiguration());
        Assertions.assertEquals(1, configuration.getAssembleOperations().size());

        AssembleOperation assembleOperation = configuration.getAssembleOperations().get(0);
        Assertions.assertEquals(beanReflexAssembler, assembleOperation.getAssembler());
        Assertions.assertEquals(keyValueContainer, assembleOperation.getContainer());
        Assertions.assertEquals(Collections.singleton(DefaultGroup.class), assembleOperation.getGroups());
        Assertions.assertEquals("namespace", assembleOperation.getNamespace());
        Assertions.assertEquals(configuration, assembleOperation.getOwner());
        Assertions.assertEquals(1, assembleOperation.getPropertyMappings().size());

        PropertyMapping propertyMapping = assembleOperation.getPropertyMappings().get(0);
        Assertions.assertEquals("sourceRef", propertyMapping.getSource());
        Assertions.assertEquals("targetRef", propertyMapping.getReference());
        Assertions.assertEquals("exp", propertyMapping.getExp());
        Assertions.assertEquals(Integer.class, propertyMapping.getExpType());

        // 装卸操作
        Assertions.assertEquals(1, configuration.getDisassembleOperations().size());
        DisassembleOperation disassembleOperation = configuration.getDisassembleOperations().get(0);
        Assertions.assertNotEquals(configuration, disassembleOperation.getOwner());
        Assertions.assertEquals(configuration.getTargetClass(), disassembleOperation.getOwner().getTargetClass());
        Assertions.assertEquals(beanReflexDisassembler, disassembleOperation.getDisassembler());

        OperationConfiguration disassembleConfiguration = disassembleOperation.getTargetOperateConfiguration();
        Assertions.assertFalse(DisassembleOperation.isDynamic(disassembleOperation));
        Assertions.assertEquals(globalConfiguration, disassembleConfiguration.getGlobalConfiguration());
        Assertions.assertEquals(NestExample.class, disassembleConfiguration.getTargetClass());
        Assertions.assertEquals(1, disassembleConfiguration.getAssembleOperations().size());
        Assertions.assertEquals(0, disassembleConfiguration.getDisassembleOperations().size());

    }

    // ===================== Example =====================

    @Operations(
        enableExtend = true,
        extendExcludes = ExcludedExampleConfigInterface.class
    )
    private static class Example implements ExampleConfigInterface {

        @Assemble(
            container = KeyValueContainer.class,
            assembler = BeanReflexAssembler.class,
            groups = DefaultGroup.class,
            namespace = "namespace",
            props = @Prop(src = "sourceRef", ref = "targetRef", exp = "exp", expType = Integer.class)
        )
        private Integer id;
        private String targetRef;

        private List<NestExample> exampleList;

    }

    @Operations(disassembles = @Disassemble(
        key = "exampleList",
        targetClass = NestExample.class,
        disassembler = BeanReflexDisassembler.class,
        useCurrParser = false,
        parser = FieldAnnotationConfigurationParser.class
    ))
    private interface ExampleConfigInterface extends ExcludedExampleConfigInterface {
        // 该接口配置应当在解析Example时一并解析
    }

    @Operations(assembles = @Assemble(
        key = "id",
        container = MethodSourceContainer.class,
        assembler = BeanReflexAssembler.class,
        groups = DefaultGroup.class,
        namespace = "namespace",
        props = @Prop(src = "sourceRef", ref = "targetRef", exp = "exp", expType = Integer.class)
    ))
    private interface ExcludedExampleConfigInterface {
        // 该接口配置应当不被解析
    }

    // ===================== NestExample =====================

    @Operations(
        enableExtend = true
    )
    private static class NestExample implements NestExampleConfigInterface {

        @Assemble(
            container = MethodSourceContainer.class,
            assembler = BeanReflexAssembler.class,
            groups = DefaultGroup.class,
            namespace = "namespace",
            props = @Prop(src = "sourceRef", ref = "targetRef", exp = "exp", expType = Integer.class)
        )
        private Integer id;
        private String targetRef;

        private List<Example> exampleList;

    }

    @Operations(disassembles = @Disassemble(
        key = "exampleList",
        targetClass = Example.class,
        disassembler = BeanReflexDisassembler.class
    ))
    private interface NestExampleConfigInterface {
        // 该接口配置应当在解析NestExample时一并解析
    }

}
