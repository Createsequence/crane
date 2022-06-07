package top.xiajibagao.crane.jackson.impl.handler;

import cn.hutool.core.text.CharSequenceUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandler;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.Operation;
import top.xiajibagao.crane.core.parser.interfaces.PropertyMapping;
import top.xiajibagao.crane.jackson.impl.helper.JacksonUtils;

import java.util.Objects;

/**
 * 处理{@link ObjectNode}类型的数据源节点对象与待处理目标节点对象
 *
 * @since 0.2.0
 * @author huangchengxing
 * @date 2022/04/12 13:07
 */
public class ObjectNodeOperateHandler extends AbstractJacksonNodeOperateHandler implements OperateHandler {

    public ObjectNodeOperateHandler(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public boolean sourceCanRead(Object source, PropertyMapping property, Operation operation) {
        return Objects.nonNull(source);
    }

    @Override
    public JsonNode readFromSource(Object source, PropertyMapping property, Operation operation) {
        if (Objects.isNull(source)) {
            return NullNode.getInstance();
        }
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
