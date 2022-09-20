package io.github.createsequence.crane.jackson.impl.handler;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.stream.StreamUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import io.github.createsequence.crane.core.annotation.ProcessorComponent;
import io.github.createsequence.crane.core.operator.interfaces.OperateProcessor;
import io.github.createsequence.crane.core.operator.interfaces.OperateProcessorComponent;
import io.github.createsequence.crane.core.parser.interfaces.AssembleOperation;
import io.github.createsequence.crane.core.parser.interfaces.Operation;
import io.github.createsequence.crane.core.parser.interfaces.PropertyMapping;
import io.github.createsequence.crane.jackson.impl.helper.JacksonUtils;

import java.util.Collection;
import java.util.Objects;

/**
 * {@link ArrayNode}节点处理器
 *
 * @since 0.2.0
 * @author huangchengxing
 * @date 2022/04/12 13:07
 */
@ProcessorComponent(OperateProcessorComponent.OPERATE_GROUP_JSON_BEAN)
public class ArrayNodeOperateHandler extends AbstractJacksonNodeOperateHandler {

    public ArrayNodeOperateHandler(ObjectMapper objectMapper, OperateProcessor operateProcessor, String... defaultRegisterGroups) {
        super(objectMapper, operateProcessor, defaultRegisterGroups);
    }

    @Override
    public boolean sourceCanRead(Object source, PropertyMapping property, Operation operation) {
        return source instanceof ArrayNode || source instanceof Collection || (Objects.nonNull(source) && source.getClass().isArray());
    }

    @Override
    public JsonNode readFromSource(Object source, PropertyMapping property, Operation operation) {
        JsonNode sourceNode = JacksonUtils.valueToTree(source);
        if (sourceNode.isEmpty()) {
            return NullNode.getInstance();
        }
        Assert.isTrue(sourceNode.isArray(), "值[{}]不是或无法解析为Json数组", source);

        // 没有数据源字段，直接返回json数组
        if (!property.hasResource()) {
            return sourceNode;
        }
        // 有数据源字段，获取json数组中的元素，并进一步处理
        ArrayNode arrayNode = objectMapper.getNodeFactory().arrayNode();
        StreamUtil.of(sourceNode)
            .map(node -> operateProcessor.tryReadFromSource(node, property, operation))
            .map(JsonNode.class::cast)
            .filter(JacksonUtils::isNotNull)
            .forEach(arrayNode::add);
        return arrayNode;
    }

    @Override
    public boolean targetCanWrite(Object sourceData, Object target, PropertyMapping property, AssembleOperation operation) {
        return target instanceof ArrayNode;
    }

    @Override
    public void writeToTarget(Object sourceData, Object target, PropertyMapping property, AssembleOperation operation) {
        ArrayNode targetNode = (ArrayNode) target;
        if (targetNode.isEmpty()) {
            return;
        }
        targetNode.forEach(node -> operateProcessor.tryWriteToTarget(sourceData, node, property, operation));
    }

}
