package top.xiajibagao.crane.jackson.impl.handler;

import cn.hutool.core.text.CharSequenceUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.util.ClassUtils;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandler;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.AssembleProperty;
import top.xiajibagao.crane.core.parser.interfaces.Operation;
import top.xiajibagao.crane.jackson.impl.helper.JacksonUtils;

import java.util.Objects;

/**
 * 处理{@link ObjectNode}类型的数据源节点对象与待处理目标节点对象
 *
 * @author huangchengxing
 * @date 2022/04/12 13:07
 */
public class ObjectNodeOperateHandler extends AbstractJacksonNodeOperateHandler implements OperateHandler {

    public ObjectNodeOperateHandler(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public boolean sourceCanRead(Object source, AssembleProperty property, Operation operation) {
        return Objects.nonNull(source) && ClassUtils.isAssignable(ObjectNode.class, source.getClass());
    }

    @Override
    public boolean targetCanWrite(Object sourceData, Object target, AssembleProperty property, AssembleOperation operation) {
        return Objects.nonNull(target) && ClassUtils.isAssignable(ObjectNode.class, target.getClass());
    }

    @Override
    public Object readFromSource(Object source, AssembleProperty property, Operation operation) {
        if (Objects.isNull(source) || !(source instanceof ObjectNode) || JacksonUtils.isNull((JsonNode)source)) {
            return NullNode.getInstance();
        }
        return property.hasResource() ?
            parse(source).get(translatePropertyName(property.getResource())) : source;
    }

    @Override
    public void writeToTarget(Object sourceData, Object target, AssembleProperty property, AssembleOperation operation) {
        if (Objects.isNull(sourceData) || !(sourceData instanceof JsonNode) || JacksonUtils.isNull((JsonNode)target)) {
            return;
        }
        // 指定了引用字段
        ObjectNode targetNode = parse(target);
        if (property.hasReference()) {
            String translatedReferenceName = translatePropertyName(property.getReference());
            parse(target).set(translatedReferenceName, (JsonNode)sourceData);
            return;
        }
        // 未指定引用字段
        String nodeName = findNodeName(targetNode, operation.getTargetProperty().getName(), operation.getTargetPropertyAliases());
        if (CharSequenceUtil.isNotBlank(nodeName)) {
            targetNode.set(nodeName, (JsonNode)sourceData);
        }
    }

    private ObjectNode parse(Object target) {
        return (ObjectNode) target;
    }

}
