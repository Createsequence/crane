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
 * @date 2022/05/30 14:58
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CoreTestConfig.class)
public class ClassAnnotationParserTest {

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
    public void testParseFieldAnnotation() {
        OperateConfigurationParser parser = new ClassAnnotationConfigurationParser(globalConfiguration, beanFactory);
        OperationConfiguration configuration = parser.parse(Example.class);

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
        Assertions.assertFalse(DisassembleOperation.isDynamic(disassembleOperation));
        Assertions.assertEquals(configuration, disassembleOperation.getOwner());
        Assertions.assertEquals(beanReflexDisassembler, disassembleOperation.getDisassembler());

        OperationConfiguration disassembleConfiguration = disassembleOperation.getTargetOperateConfiguration();
        Assertions.assertEquals(globalConfiguration, disassembleConfiguration.getGlobalConfiguration());
        Assertions.assertEquals(NestExample.class, disassembleConfiguration.getTargetClass());
        Assertions.assertEquals(1, disassembleConfiguration.getAssembleOperations().size());

        AssembleOperation disassembleConfigurationAssembleOperation = disassembleConfiguration.getAssembleOperations().get(0);
        Assertions.assertEquals(beanReflexAssembler, disassembleConfigurationAssembleOperation.getAssembler());
        Assertions.assertEquals(methodSourceContainer, disassembleConfigurationAssembleOperation.getContainer());
        Assertions.assertEquals(Collections.singleton(DefaultGroup.class), disassembleConfigurationAssembleOperation.getGroups());
        Assertions.assertEquals("namespace", disassembleConfigurationAssembleOperation.getNamespace());
        Assertions.assertEquals(disassembleConfiguration, disassembleConfigurationAssembleOperation.getOwner());
        Assertions.assertEquals(1, disassembleConfigurationAssembleOperation.getPropertyMappings().size());

        PropertyMapping disassembleConfigurationPropertyMapping = assembleOperation.getPropertyMappings().get(0);
        Assertions.assertEquals("sourceRef", disassembleConfigurationPropertyMapping.getSource());
        Assertions.assertEquals("targetRef", disassembleConfigurationPropertyMapping.getReference());
        Assertions.assertEquals("exp", disassembleConfigurationPropertyMapping.getExp());
        Assertions.assertEquals(Integer.class, disassembleConfigurationPropertyMapping.getExpType());

        Assertions.assertEquals(2, disassembleConfiguration.getDisassembleOperations().size());
        Assertions.assertEquals(configuration, disassembleConfiguration.getDisassembleOperations().get(1).getTargetOperateConfiguration());
        Assertions.assertTrue(DisassembleOperation.isDynamic(disassembleConfiguration.getDisassembleOperations().get(0)));

    }

    // ===================== Example =====================

    @Operations(
        assembles = @Assemble(
            key = "id",
            container = KeyValueContainer.class,
            assembler = BeanReflexAssembler.class,
            groups = DefaultGroup.class,
            namespace = "namespace",
            props = @Prop(src = "sourceRef", ref = "targetRef", exp = "exp", expType = Integer.class)
        ),
        enableExtend = true,
        extendExcludes = ExcludedExampleConfigInterface.class
    )
    private static class Example implements ExampleConfigInterface {

        private Integer id;
        private String targetRef;

        private List<NestExample> exampleList;

    }

    @Operations(disassembles = @Disassemble(
        key = "exampleList",
        targetClass = NestExample.class,
        disassembler = BeanReflexDisassembler.class
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
        assembles = @Assemble(
            key = "id",
            container = MethodSourceContainer.class,
            assembler = BeanReflexAssembler.class,
            groups = DefaultGroup.class,
            namespace = "namespace",
            props = @Prop(src = "sourceRef", ref = "targetRef", exp = "exp", expType = Integer.class)
        ),
        disassembles = @Disassemble(key = "dynamicList"),
        enableExtend = true
    )
    private static class NestExample implements NestExampleConfigInterface {

        private Integer id;
        private String targetRef;

        private List<Example> exampleList;
        private List<?> dynamicList;

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
