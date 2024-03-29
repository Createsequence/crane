package io.github.createsequence.crane.core.parser;

import io.github.createsequence.crane.core.container.Container;
import io.github.createsequence.crane.core.operator.interfaces.Assembler;
import io.github.createsequence.crane.core.parser.interfaces.AssembleOperation;
import io.github.createsequence.crane.core.parser.interfaces.OperationConfiguration;
import io.github.createsequence.crane.core.parser.interfaces.PropertyMapping;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

/**
 * {@link AssembleOperation}的通用实现
 *
 * @author huangchengxing
 * @date 2022/03/01 16:07
 */
@Getter
@RequiredArgsConstructor
public class BeanAssembleOperation implements AssembleOperation {

    private final int order;
    private final OperationConfiguration owner;
    private final Field targetProperty;
    private final Set<String> targetPropertyAliases;
    private final String namespace;
    private final Container container;
    private final Assembler assembler;
    private final List<PropertyMapping> propertyMappings;
    private final Set<Class<?>> groups;

}
