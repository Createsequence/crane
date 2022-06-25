package top.xiajibagao.crane.jackson.impl.handler;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandler;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.jackson.impl.helper.JacksonUtils;

import java.util.Collection;

/**
 * {@link OperateHandler}的基础实现，提供了一些针对{@link JsonNode}类型对象的操作方法和必要的组件
 *
 * @author huangchengxing
 * @date 2022/04/12 13:23
 * @since 0.2.0
 */
@RequiredArgsConstructor
public abstract class AbstractJacksonNodeOperateHandler implements OperateHandler {

    protected final ObjectMapper objectMapper;

    /**
     * 先寻找ObjectMapper的命名规则处理后的指定字段名对应的节点，若不存在，则再根据别名寻找至少一个存在的别名字段对应的节点
     *
     * @param target 目标
     * @param targetName 操作配置
     * @return com.fasterxml.jackson.databind.JsonNode
     * @author huangchengxing
     * @date 2022/3/2 11:25
     */
    protected JsonNode findNode(JsonNode target, String targetName, Collection<String> aliases) {
        String keyPropertyName = translatePropertyName(targetName);
        JsonNode keyNode = target.get(keyPropertyName);
        if (JacksonUtils.isNotNull(keyNode)) {
            return keyNode;
        }
        return CollUtil.getFirst(
            JacksonUtils.findNodes(target, aliases)
        );
    }

    /**
     * 先寻找ObjectMapper的命名规则处理后的指定字段名对应的节点，若不存在，则再根据别名寻找至少一个存在的别名字段对应的节点
     *
     * @param target 目标
     * @param targetName 操作配置
     * @return com.fasterxml.jackson.databind.JsonNode
     * @author huangchengxing
     * @date 2022/3/2 11:25
     */
    protected String findNodeName(JsonNode target, String targetName, Collection<String> aliases) {
        String keyPropertyName = translatePropertyName(targetName);
        if (target.has(keyPropertyName)) {
            return targetName;
        }
        if (CollUtil.isEmpty(aliases)) {
            return null;
        }
        return aliases.stream()
            .filter(target::has)
            .findFirst()
            .orElse(null);
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
