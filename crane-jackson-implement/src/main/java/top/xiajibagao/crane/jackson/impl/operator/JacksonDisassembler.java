package top.xiajibagao.crane.jackson.impl.operator;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import top.xiajibagao.crane.core.exception.CraneException;
import top.xiajibagao.crane.core.operator.interfaces.Disassembler;
import top.xiajibagao.crane.core.parser.interfaces.DisassembleOperation;
import top.xiajibagao.crane.jackson.impl.helper.JacksonUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author huangchengxing
 * @date 2022/03/02 10:04
 */
@RequiredArgsConstructor
public class JacksonDisassembler implements Disassembler {

    private final ObjectMapper objectMapper;

    @Override
    public Collection<?> execute(Object target, DisassembleOperation operation) {
        if (!(target instanceof JsonNode) || ((JsonNode)target).isNull()) {
            return Collections.emptyList();
        }
        JsonNode targetNode = (JsonNode)target;
        JsonNode targetPropertyNode = findTargetPropertyNode(targetNode, operation);
        if (JacksonUtils.isNull(targetPropertyNode)) {
            return Collections.emptyList();
        }

        // 处理数据
        List<JsonNode> results = new ArrayList<>();
        if (targetPropertyNode.isArray()) {
            targetPropertyNode.forEach(results::add);
        } else if(targetPropertyNode.isObject()) {
            results.add(targetPropertyNode);
        } else {
            CraneException.throwOf(
                "对象[{}]的节点[{}]，无法拆卸为json数组或json对象",
                targetNode, targetPropertyNode
            );
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
