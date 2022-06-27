package top.xiajibagao.crane.jackson.impl.handler;

import cn.hutool.core.text.CharSequenceUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import top.xiajibagao.crane.core.annotation.GroupRegister;
import top.xiajibagao.crane.core.operator.interfaces.OperateProcessor;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.Operation;
import top.xiajibagao.crane.core.parser.interfaces.PropertyMapping;
import top.xiajibagao.crane.jackson.impl.helper.JacksonUtils;

import java.util.Objects;

/**
 * {@link ObjectNode}节点处理器
 *
 * @since 0.2.0
 * @author huangchengxing
 * @date 2022/04/12 13:07
 */
@GroupRegister(OperateProcessor.OPERATE_GROUP_JSON_BEAN)
public class ObjectNodeOperateHandler extends AbstractJacksonNodeOperateHandler {

    public ObjectNodeOperateHandler(ObjectMapper objectMapper, OperateProcessor operateProcessor) {
        super(objectMapper, operateProcessor);
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
