package top.xiajibagao.crane.jackson.impl.operator;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import top.xiajibagao.crane.core.exception.CraneException;
import top.xiajibagao.crane.core.handler.OperateHandlerChain;
import top.xiajibagao.crane.core.helper.PairEntry;
import top.xiajibagao.crane.core.operator.interfaces.Assembler;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.AssembleProperty;
import top.xiajibagao.crane.jackson.impl.helper.JacksonUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author huangchengxing
 * @date 2022/03/02 10:03
 */
@Getter
@RequiredArgsConstructor
public class JacksonAssembler implements Assembler {

    protected final ObjectMapper objectMapper;
    private final OperateHandlerChain handlerChain;

    @Override
    public void execute(Object target, Object source, AssembleOperation operation) {
        if (Objects.isNull(source) || !(target instanceof ObjectNode)) {
            return;
        }
        ObjectNode targetNode = (ObjectNode) target;
        JsonNode sourceNode = (source instanceof JsonNode) ?
            (JsonNode)source : objectMapper.valueToTree(source);
        CollUtil.defaultIfEmpty(operation.getProperties(), Collections.singletonList(AssembleProperty.empty()))
            .stream()
            .map(property -> PairEntry.of(property, handlerChain.readFromSource(source, property, operation)))
            .filter(PairEntry::hasValue)
            .forEach(pair -> handlerChain.writeToTarget(pair.getValue(), target, pair.getKey(), operation));






        // 存在字段配置
        // TODO 此处字段处理策略应当可以自定义，或改为从特定容器中获取
        operation.getProperties().forEach(prop -> {
            if (sourceNode.isObject()) {
                processPropertyIfObjectSourceNode(targetNode, (ObjectNode) sourceNode, prop, operation);
            } else if (sourceNode.isValueNode()) {
                processPropertyIfValueSourceNode(targetNode, (ValueNode) sourceNode, prop, operation);
            } else if (sourceNode.isArray()) {
                processPropertyIfArraySourceNode(targetNode, (ArrayNode) sourceNode, prop, operation);
            } else {
                CraneException.throwOf("节点[%s]为无法处理的节点类型:[%s]", sourceNode, sourceNode.getNodeType());
            }
        });
    }

    /**
     * 若无具体字段配置，则将注解字段以及别名在目标节点中对应的字段节点全部替换为从容器中获取的数据源对应节点
     *
     * @param target 目标节点
     * @param source 数据源节点
     * @param operation 操作配置
     * @author huangchengxing
     * @date 2022/2/26 10:47
     */
    protected void processIfNonProperties(ObjectNode target, JsonNode source, AssembleOperation operation) {
        Set<String> searchNames = new HashSet<>(operation.getTargetPropertyAliases());
        searchNames.add(getTranslatedKeyPropertyName(operation));
        for (String alias : searchNames) {
            target.replace(alias, source);
        }
    }

    /**
     * 若数据源节点为对象类型，则根据字段配置将其字段替换或追加到当前目标节点中
     *
     * @param target 目标节点
     * @param source 数据源节点
     * @param property 字段配置
     * @param operation 操作配置
     * @author huangchengxing
     * @date 2022/2/26 10:47
     */
    protected void processPropertyIfObjectSourceNode(ObjectNode target, ObjectNode source, AssembleProperty property, AssembleOperation operation) {
        String targetProperty = translatePropertyName(property.getReference());
        if (StringUtils.hasText(property.getResource())) {
            // 设置了引用字段
            String sourceProperty = translatePropertyName(property.getResource());
            target.set(targetProperty, source.get(sourceProperty));
        } else {
            target.set(targetProperty, source);
        }
    }

    /**
     * 若数据源节点为值类型，则根据字段配置将其字段替换或追加到当前目标节点中
     *
     * @param target 目标节点
     * @param source 数据源节点
     * @param property 字段配置
     * @param operation 操作配置
     * @author huangchengxing
     * @date 2022/2/26 10:47
     */
    protected void processPropertyIfValueSourceNode(ObjectNode target, ValueNode source, AssembleProperty property, AssembleOperation operation) {
        String targetProperty = translatePropertyName(property.getReference());
        target.set(targetProperty, source);
    }

    /**
     * 若数据源节点为集合类型，则根据字段配置将其字段替换或追加到当前目标节点中
     *
     * @param target 目标节点
     * @param source 数据源节点
     * @param property 字段配置
     * @param operation 操作配置
     * @author huangchengxing
     * @date 2022/2/26 10:47
     */
    protected void processPropertyIfArraySourceNode(ObjectNode target, ArrayNode source, AssembleProperty property, AssembleOperation operation) {
        String targetProperty = translatePropertyName(property.getReference());
        target.set(targetProperty, source);
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
