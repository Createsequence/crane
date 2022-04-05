package top.xiajibagao.crane.core.parser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.xiajibagao.crane.core.container.Container;
import top.xiajibagao.crane.core.operator.interfaces.Assembler;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.AssembleProperty;
import top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

/**
 * @author huangchengxing
 * @date 2022/03/01 16:07
 */
@Getter
@RequiredArgsConstructor
public class BeanAssembleOperation implements AssembleOperation {

    private final int sort;
    private final OperationConfiguration owner;
    private final Field targetProperty;
    private final Set<String> targetPropertyAliases;
    private final String namespace;
    private final Container container;
    private final Assembler assembler;
    private final List<AssembleProperty> properties;

}
