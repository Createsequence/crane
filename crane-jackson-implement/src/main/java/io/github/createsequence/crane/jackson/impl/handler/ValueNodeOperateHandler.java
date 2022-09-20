package io.github.createsequence.crane.jackson.impl.handler;

import cn.hutool.core.util.ClassUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ValueNode;
import io.github.createsequence.crane.core.annotation.ProcessorComponent;
import io.github.createsequence.crane.core.operator.interfaces.OperateProcessor;
import io.github.createsequence.crane.core.operator.interfaces.OperateProcessorComponent;
import io.github.createsequence.crane.core.parser.interfaces.AssembleOperation;
import io.github.createsequence.crane.core.parser.interfaces.Operation;
import io.github.createsequence.crane.core.parser.interfaces.PropertyMapping;

import java.util.Objects;

/**
 * {@link ValueNode}节点处理器
 *
 * @since 0.2.0
 * @author huangchengxing
 * @date 2022/04/12 13:07
 */
@ProcessorComponent(OperateProcessorComponent.OPERATE_GROUP_JSON_BEAN)
public class ValueNodeOperateHandler extends AbstractJacksonNodeOperateHandler {

    public ValueNodeOperateHandler(ObjectMapper objectMapper, OperateProcessor operateProcessor, String... defaultRegisterGroups) {
        super(objectMapper, operateProcessor, defaultRegisterGroups);
    }

    @Override
    public boolean sourceCanRead(Object source, PropertyMapping property, Operation operation) {
        if (Objects.isNull(source)) {
            return false;
        }
        return source instanceof ValueNode || ClassUtil.isBasicType(source.getClass()) || source instanceof String;
    }

    @Override
    public JsonNode readFromSource(Object source, PropertyMapping property, Operation operation) {
        // 值节点总是返回他本身
        return objectMapper.valueToTree(source);
    }

    @Override
    public boolean targetCanWrite(Object sourceData, Object target, PropertyMapping property, AssembleOperation operation) {
        return target instanceof ValueNode;
    }

    @Override
    public void writeToTarget(Object sourceData, Object target, PropertyMapping property, AssembleOperation operation) {
        // 值节点无法做任何处理
    }

}
