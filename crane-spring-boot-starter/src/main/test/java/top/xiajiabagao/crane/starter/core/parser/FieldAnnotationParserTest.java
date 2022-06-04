package top.xiajiabagao.crane.starter.core.parser;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.xiajiabagao.crane.starter.core.ExampleConfig;
import top.xiajibagao.crane.core.annotation.Assemble;
import top.xiajibagao.crane.core.annotation.Disassemble;
import top.xiajibagao.crane.core.annotation.Prop;
import top.xiajibagao.crane.core.container.KeyValueContainer;
import top.xiajibagao.crane.core.container.MethodSourceContainer;
import top.xiajibagao.crane.core.helper.DefaultGroup;
import top.xiajibagao.crane.core.operator.BeanReflexAssembler;
import top.xiajibagao.crane.core.operator.BeanReflexDisassembler;
import top.xiajibagao.crane.core.parser.FieldAnnotationConfigurationParser;
import top.xiajibagao.crane.core.parser.interfaces.*;

import java.util.Collections;
import java.util.List;

/**
 * @author huangchengxing
 * @date 2022/05/30 14:24
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExampleConfig.class)
public class FieldAnnotationParserTest {

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
        OperateConfigurationParser parser = new FieldAnnotationConfigurationParser(globalConfiguration, beanFactory);
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
        Assertions.assertEquals(1, assembleOperation.getProperties().size());

        PropertyMapping propertyMapping = assembleOperation.getProperties().get(0);
        Assertions.assertEquals("sourceRef", propertyMapping.getSource());
        Assertions.assertEquals("targetRef", propertyMapping.getReference());
        Assertions.assertEquals("exp", propertyMapping.getExp());
        Assertions.assertEquals(Integer.class, propertyMapping.getExpType());

        // 装卸操作
        Assertions.assertEquals(1, configuration.getDisassembleOperations().size());
        DisassembleOperation disassembleOperation = configuration.getDisassembleOperations().get(0);
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
        Assertions.assertEquals(1, disassembleConfigurationAssembleOperation.getProperties().size());

        PropertyMapping disassembleConfigurationPropertyMapping = assembleOperation.getProperties().get(0);
        Assertions.assertEquals("sourceRef", disassembleConfigurationPropertyMapping.getSource());
        Assertions.assertEquals("targetRef", disassembleConfigurationPropertyMapping.getReference());
        Assertions.assertEquals("exp", disassembleConfigurationPropertyMapping.getExp());
        Assertions.assertEquals(Integer.class, disassembleConfigurationPropertyMapping.getExpType());

        Assertions.assertEquals(1, disassembleConfiguration.getDisassembleOperations().size());
        Assertions.assertEquals(configuration, disassembleConfiguration.getDisassembleOperations().get(0).getTargetOperateConfiguration());

    }

    private static class Example {

        @Assemble(
            container = KeyValueContainer.class,
            assembler = BeanReflexAssembler.class,
            groups = DefaultGroup.class,
            namespace = "namespace",
            props = @Prop(src = "sourceRef", ref = "targetRef", exp = "exp", expType = Integer.class)
        )
        private Integer id;
        private String targetRef;

        @Disassemble(
            targetClass = NestExample.class,
            disassembler = BeanReflexDisassembler.class
        )
        private List<NestExample> exampleList;

        public Example() {
        }
    }

    private static class NestExample {

        @Assemble(
            container = MethodSourceContainer.class,
            assembler = BeanReflexAssembler.class,
            groups = DefaultGroup.class,
            namespace = "namespace",
            props = @Prop(src = "sourceRef", ref = "targetRef", exp = "exp", expType = Void.class)
        )
        private Integer id;
        private String targetRef;

        @Disassemble(
            targetClass = Example.class,
            disassembler = BeanReflexDisassembler.class
        )
        private List<Example> exampleList;

    }

}
