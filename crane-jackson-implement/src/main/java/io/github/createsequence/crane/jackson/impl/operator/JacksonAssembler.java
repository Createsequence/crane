package io.github.createsequence.crane.jackson.impl.operator;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.createsequence.crane.core.helper.PairEntry;
import io.github.createsequence.crane.core.operator.interfaces.Assembler;
import io.github.createsequence.crane.core.operator.interfaces.OperateProcessor;
import io.github.createsequence.crane.core.parser.EmptyPropertyMapping;
import io.github.createsequence.crane.core.parser.interfaces.AssembleOperation;
import io.github.createsequence.crane.jackson.impl.helper.JacksonUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Objects;

/**
 * {@link JsonNode}数据装配器
 *
 * @author huangchengxing
 * @date 2022/03/02 10:03
 * @see JacksonOperateProcessor
 */
@Getter
@RequiredArgsConstructor
public class JacksonAssembler implements Assembler {

    protected final ObjectMapper objectMapper;
    private final OperateProcessor operateProcessor;

    @Override
    public void execute(Object target, Object source, AssembleOperation operation) {
        if (JacksonUtils.isNotNodeOrNull(target) || Objects.isNull(source)) {
            return;
        }
        ObjectNode targetNode = (ObjectNode) target;
        JsonNode sourceNode = (source instanceof JsonNode) ?
            (JsonNode)source : objectMapper.valueToTree(source);
        CollUtil.defaultIfEmpty(operation.getPropertyMappings(), Collections.singletonList(EmptyPropertyMapping.instance()))
            .stream()
            .map(property -> PairEntry.of(property, operateProcessor.readFromSource(sourceNode, property, operation)))
            .filter(PairEntry::hasValue)
            .forEach(pair -> operateProcessor.writeToTarget(pair.getValue(), targetNode, pair.getKey(), operation));
    }

    /**
     * 找到JsonNode中注解字段对应的节点，并返回其{@link JsonNode#asText()}。<br />
     *
     * @param target 目标实例节点
     * @param operation 操作配置
     * @return java.lang.Object
     * @author huangchengxing
     * @date 2022/3/2 11:27
     */
    @Override
    public Object getKey(Object target, AssembleOperation operation) {
        if (!(target instanceof JsonNode)) {
            return null;
        }
        JsonNode targetNode = (JsonNode)target;
        JsonNode keyProperty = findKeyNode(targetNode, operation);
        return Objects.isNull(keyProperty) ? null : keyProperty.asText();
    }

    /**
     * 先寻找ObjectMapper的命名规则处理后的key字段名对应的节点，若不存在，则再根据别名寻找至少一个存在的别名字段对应的节点
     *
     * @param target 目标
     * @param operation 操作配置
     * @return com.fasterxml.jackson.databind.JsonNode
     * @author huangchengxing
     * @date 2022/3/2 11:25
     */
    protected JsonNode findKeyNode(JsonNode target, AssembleOperation operation) {
        String keyPropertyName = getTranslatedKeyPropertyName(operation);
        JsonNode keyNode = target.get(keyPropertyName);
        if (JacksonUtils.isNotNull(keyNode)) {
            return keyNode;
        }
        return CollUtil.getFirst(
            JacksonUtils.findNodes(target, operation.getTargetPropertyAliases())
        );
    }
    
    /**
     * 将字段名按ObjectMapper的配置转换
     *
     * @param defName 默认属性名
     * @return java.lang.String
     * @author huangchengxing
     * @date 2022/3/2 11:25
     */
    protected String translatePropertyName(String defName) {
        return JacksonUtils.translatePropertyName(objectMapper, defName);
    }

    /**
     * 将key字段名按ObjectMapper的配置转换
     *
     * @param operation 默认属性名
     * @return java.lang.String
     * @author huangchengxing
     * @date 2022/3/2 11:25
     */
    protected String getTranslatedKeyPropertyName(AssembleOperation operation) {
        return translatePropertyName(operation.getTargetProperty().getName());
    }

}
