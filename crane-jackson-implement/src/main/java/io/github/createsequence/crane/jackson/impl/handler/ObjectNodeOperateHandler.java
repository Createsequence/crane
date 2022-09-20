package io.github.createsequence.crane.jackson.impl.handler;

import cn.hutool.core.text.CharSequenceUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.createsequence.crane.core.annotation.ProcessorComponent;
import io.github.createsequence.crane.core.operator.interfaces.OperateProcessor;
import io.github.createsequence.crane.core.operator.interfaces.OperateProcessorComponent;
import io.github.createsequence.crane.core.parser.interfaces.AssembleOperation;
import io.github.createsequence.crane.core.parser.interfaces.Operation;
import io.github.createsequence.crane.core.parser.interfaces.PropertyMapping;
import io.github.createsequence.crane.jackson.impl.helper.JacksonUtils;

import java.util.Objects;

/**
 * {@link ObjectNode}节点处理器
 *
 * @since 0.2.0
 * @author huangchengxing
 * @date 2022/04/12 13:07
 */
@ProcessorComponent(OperateProcessorComponent.OPERATE_GROUP_JSON_BEAN)
public class ObjectNodeOperateHandler extends AbstractJacksonNodeOperateHandler {

    public ObjectNodeOperateHandler(ObjectMapper objectMapper, OperateProcessor operateProcessor, String... defaultRegisterGroups) {
        super(objectMapper, operateProcessor, defaultRegisterGroups);
    }

    @Override
    public boolean sourceCanRead(Object source, PropertyMapping property, Operation operation) {
        return Objects.nonNull(source);
    }

    @Override
    public JsonNode readFromSource(Object source, PropertyMapping property, Operation operation) {
        JsonNode sourceNode = JacksonUtils.valueToTree(objectMapper, source);
        if (sourceNode.isObject()) {
            return property.hasResource() ?
                sourceNode.get(translatePropertyName(property.getSource())) : sourceNode;
        }
        return property.hasResource() ? NullNode.getInstance() : sourceNode;
    }

    @Override
    public boolean targetCanWrite(Object sourceData, Object target, PropertyMapping property, AssembleOperation operation) {
        return target instanceof ObjectNode;
    }

    @Override
    public void writeToTarget(Object sourceData, Object target, PropertyMapping property, AssembleOperation operation) {
        // 指定了引用字段
        ObjectNode targetNode = (ObjectNode) target;
        if (property.hasReference()) {
            String translatedReferenceName = translatePropertyName(property.getReference());
            JsonNode sourceNode = JacksonUtils.valueToTree(objectMapper, sourceData);
            targetNode.set(translatedReferenceName, sourceNode);
            return;
        }
        // 未指定引用字段
        String nodeName = findNodeName(targetNode, operation.getTargetProperty().getName(), operation.getTargetPropertyAliases());
        if (CharSequenceUtil.isNotBlank(nodeName)) {
            JsonNode sourceNode = JacksonUtils.valueToTree(objectMapper, sourceData);
            targetNode.set(nodeName, sourceNode);
        }
    }

}
