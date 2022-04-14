package top.xiajibagao.crane.jackson.impl.handler;

import cn.hutool.core.stream.StreamUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import org.springframework.util.ClassUtils;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandler;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandlerChain;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.AssembleProperty;
import top.xiajibagao.crane.core.parser.interfaces.Operation;
import top.xiajibagao.crane.jackson.impl.helper.JacksonUtils;

import java.util.Objects;

/**
 * @author huangchengxing
 * @date 2022/04/12 13:07
 */
public class ArrayNodeOperateHandler extends AbstractJacksonNodeOperateHandler implements OperateHandler {

    private final OperateHandlerChain handlerChain;

    public ArrayNodeOperateHandler(ObjectMapper objectMapper, OperateHandlerChain assembleHandlerChain) {
        super(objectMapper);
        this.handlerChain = assembleHandlerChain;
    }

    @Override
    public boolean sourceCanRead(Object source, AssembleProperty property, Operation operation) {
        return Objects.nonNull(source) && ClassUtils.isAssignable(ArrayNode.class, source.getClass());
    }

    @Override
    public boolean targetCanWrite(Object sourceData, Object target, AssembleProperty property, AssembleOperation operation) {
        return Objects.nonNull(target) && ClassUtils.isAssignable(ArrayNode.class, target.getClass());
    }

    @Override
    public Object readFromSource(Object source, AssembleProperty property, Operation operation) {
        if (Objects.isNull(source) || !(source instanceof ArrayNode) || JacksonUtils.isNull((JsonNode)source)) {
            return NullNode.getInstance();
        }
        ArrayNode sourceNode = parse(source);
        if (!property.hasResource()) {
            return sourceNode;
        }
        ArrayNode arrayNode = objectMapper.getNodeFactory().arrayNode();
        StreamUtil.of(sourceNode)
            .map(t -> handlerChain.readFromSource(t, property, operation))
            .map(JsonNode.class::cast)
            .filter(JacksonUtils::isNotNull)
            .forEach(arrayNode::add);
        return arrayNode;
    }

    @Override
    public void writeToTarget(Object sourceData, Object target, AssembleProperty property, AssembleOperation operation) {
        if (Objects.isNull(target) || !(target instanceof ArrayNode) || JacksonUtils.isNull((JsonNode)target)) {
            return;
        }
        for (JsonNode jsonNode : parse(target)) {
            handlerChain.writeToTarget(sourceData, jsonNode, property, operation);
        }
    }

    public ArrayNode parse(Object target) {
        return (ArrayNode) target;
    }

}
