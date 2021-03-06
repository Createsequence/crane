package top.xiajiabagao.crane.starter.core.parser;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.xiajiabagao.crane.starter.core.CoreTestConfig;
import top.xiajibagao.crane.core.annotation.Assemble;
import top.xiajibagao.crane.core.annotation.Disassemble;
import top.xiajibagao.crane.core.annotation.Operations;
import top.xiajibagao.crane.core.annotation.Prop;
import top.xiajibagao.crane.core.container.KeyValueContainer;
import top.xiajibagao.crane.core.container.MethodSourceContainer;
import top.xiajibagao.crane.core.helper.DefaultGroup;
import top.xiajibagao.crane.core.operator.BeanReflexAssembler;
import top.xiajibagao.crane.core.operator.BeanReflexDisassembler;
import top.xiajibagao.crane.core.parser.ClassAnnotationConfigurationParser;
import top.xiajibagao.crane.core.parser.CombineOperationConfigurationParser;
import top.xiajibagao.crane.core.parser.FieldAnnotationConfigurationParser;
import top.xiajibagao.crane.core.parser.interfaces.*;

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
        // ????????????
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

        // ????????????
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
        // ??????????????????????????????Example???????????????
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
        // ?????????????????????????????????
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
        // ??????????????????????????????NestExample???????????????
    }

}
