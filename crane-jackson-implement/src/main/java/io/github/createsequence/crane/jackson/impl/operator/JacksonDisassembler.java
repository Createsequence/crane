package io.github.createsequence.crane.jackson.impl.operator;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.createsequence.crane.core.operator.interfaces.Disassembler;
import io.github.createsequence.crane.core.operator.interfaces.OperateProcessor;
import io.github.createsequence.crane.core.parser.interfaces.DisassembleOperation;
import io.github.createsequence.crane.jackson.impl.helper.JacksonUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;

/**
 * {@link JsonNode}数据装卸器
 *
 * @author huangchengxing
 * @date 2022/03/02 10:04
 * @see JacksonOperateProcessor
 */
@Getter
@RequiredArgsConstructor
public class JacksonDisassembler implements Disassembler {

    private final ObjectMapper objectMapper;
    private final OperateProcessor operateProcessor;

    @Override
    public Collection<?> execute(Object target, DisassembleOperation operation) {
        if (JacksonUtils.isNotNodeOrNull(target)) {
            return Collections.emptyList();
        }
        JsonNode targetNode = JacksonUtils.valueToTree(target);
        JsonNode targetPropertyNode = findTargetPropertyNode(targetNode, operation);
        if (JacksonUtils.isNull(targetPropertyNode)) {
            return Collections.emptyList();
        }

        // bfs遍历
        List<JsonNode> results = new ArrayList<>();
        Deque<JsonNode> deque = new LinkedList<>();
        deque.add(targetPropertyNode);
        while (!deque.isEmpty()) {
            JsonNode node = deque.removeFirst();
            // 仍然还是json数组
            if (node.isArray()) {
                node.forEach(deque::addLast);
                continue;
            }
            results.add(node);
        }
        return results;
    }

    /**
     * 先寻找ObjectMapper的命名规则处理后的target字段名对应的节点，若不存在，则再根据别名寻找至少一个存在的别名字段对应的节点
     *
     * @param target 目标
     * @param operation 操作配置
     * @return com.fasterxml.jackson.databind.JsonNode
     * @author huangchengxing
     * @date 2022/3/2 11:25
     */
    protected JsonNode findTargetPropertyNode(JsonNode target, DisassembleOperation operation) {
        String keyPropertyName = getTranslatedTargetPropertyName(operation);
        JsonNode targetNode = target.get(keyPropertyName);
        if (JacksonUtils.isNotNull(targetNode)) {
            return targetNode;
        }
        return CollUtil.getFirst(
            JacksonUtils.findNodes(target, operation.getTargetPropertyAliases())
        );
    }


    /**
     * 将key字段名按ObjectMapper的配置转换
     *
     * @param operation 默认属性名
     * @return java.lang.String
     * @author huangchengxing
     * @date 2022/3/2 11:25
     */
    protected String getTranslatedTargetPropertyName(DisassembleOperation operation) {
        return JacksonUtils.translatePropertyName(objectMapper, operation.getTargetProperty().getName());
    }

}
