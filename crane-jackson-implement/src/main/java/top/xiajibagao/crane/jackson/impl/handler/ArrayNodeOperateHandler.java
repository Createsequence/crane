package top.xiajibagao.crane.jackson.impl.handler;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.stream.StreamUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandler;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandlerChain;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.Operation;
import top.xiajibagao.crane.core.parser.interfaces.PropertyMapping;
import top.xiajibagao.crane.jackson.impl.helper.JacksonUtils;

import java.util.Collection;
import java.util.Objects;

/**
 * {@link ArrayNode}节点处理器
 *
 * @since 0.2.0
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
            .map(node -> handlerChain.tryReadFromSource(node, property, operation))
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
        targetNode.forEach(node -> handlerChain.tryWriteToTarget(sourceData, node, property, operation));
    }

}
