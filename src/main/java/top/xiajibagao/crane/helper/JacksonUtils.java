package top.xiajibagao.crane.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import top.xiajibagao.crane.exception.CraneException;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author huangchengxing
 * @date 2022/01/11 11:32
 */
@Slf4j
public class JacksonUtils {

    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();

    private JacksonUtils() {
    }

    public static <T> T getBeanByPath(ObjectMapper objectMapper, T target, String jsonPath) {
        JsonNode targetNode = beanToTree(objectMapper, target).at(jsonPath);
        try {
            return targetNode.isNull() ? null : objectMapper.readValue(targetNode.textValue(), new TypeReference<T>() {
            });
        } catch (JsonProcessingException e) {
            throw new CraneException(e);
        }
    }

    /**
     * 对象转jsonNode
     *
     * @param target 对象
     * @return com.fasterxml.jackson.databind.JsonNode
     * @author huangchengxing
     * @date 2022/1/11 11:44
     */
    public static JsonNode beanToTree(Object target) {
        return beanToTree(DEFAULT_MAPPER, target);
    }

    /**
     * 对象转jsonNode
     *
     * @param objectMapper objectMapper
     * @param target       对象
     * @return com.fasterxml.jackson.databind.JsonNode
     * @author huangchengxing
     * @date 2022/1/11 11:44
     */
    public static JsonNode beanToTree(ObjectMapper objectMapper, Object target) {
        return objectMapper.valueToTree(target);
    }

    /**
     * 对象转json字符串
     *
     * @param objectMapper objectMapper
     * @param target       对象
     * @return java.lang.String
     * @author huangchengxing
     * @date 2022/1/11 11:44
     */
    public static String beanToJson(ObjectMapper objectMapper, Object target) {
        try {
            return objectMapper.writeValueAsString(target);
        } catch (JsonProcessingException e) {
            throw new CraneException(e);
        }
    }

    /**
     * 对象转json字符串
     *
     * @param target 对象
     * @return java.lang.String
     * @author huangchengxing
     * @date 2022/1/11 11:44
     */
    public static String beanToJson(Object target) {
        return beanToJson(DEFAULT_MAPPER, target);
    }

    /**
     * json转对象
     *
     * @param objectMapper objectMapper
     * @param json         json字符串
     * @param target       对象
     * @return T
     * @author huangchengxing
     * @date 2022/1/11 11:43
     */
    public static <T> T jsonToBean(ObjectMapper objectMapper, String json, Class<T> target) {
        try {
            return objectMapper.readValue(json, target);
        } catch (JsonProcessingException e) {
            throw new CraneException(e);
        }
    }

    /**
     * json转对象
     *
     * @param json   json字符串
     * @param target 对象
     * @return T
     * @author huangchengxing
     * @date 2022/1/11 11:43
     */
    public static <T> T jsonToBean(String json, Class<T> target) {
        return jsonToBean(DEFAULT_MAPPER, json, target);
    }

    /**
     * json转对象集合
     *
     * @param objectMapper objectMapper
     * @param json         json字符串
     * @return java.util.List<T>
     * @author huangchengxing
     * @date 2022/1/11 11:43
     */
    public static <T> List<T> jsonToList(ObjectMapper objectMapper, String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<T>>() {
            });
        } catch (JsonProcessingException e) {
            throw new CraneException(e);
        }
    }

    /**
     * json转对象集合
     *
     * @param json json字符串
     * @return java.util.List<T>
     * @author huangchengxing
     * @date 2022/1/11 11:43
     */
    public static <T> List<T> jsonToList(String json) {
        return jsonToList(DEFAULT_MAPPER, json);
    }

    /**
     * 根据objectMapper配置序列化参数
     *
     * @param objectMapper objectMapper
     * @param defName 默认属性名
     * @return java.lang.String
     * @author huangchengxing
     * @date 2022/3/2 10:53
     */
    public static String translatePropertyName(ObjectMapper objectMapper, String defName) {
        return objectMapper.getPropertyNamingStrategy()
            .nameForField(objectMapper.getSerializationConfig(), null, defName);
    }
    
    /**
     * 节点是否不为null
     *
     * @param node 节点
     * @return boolean
     * @author huangchengxing
     * @date 2022/3/2 11:14
     */
    public static boolean isNotNull(JsonNode node) {
        return !isNull(node);
    }

    /**
     * 节点是否为null
     *
     * @param node 节点
     * @return boolean
     * @author huangchengxing
     * @date 2022/3/2 11:14
     */
    public static boolean isNull(JsonNode node) {
        return Objects.isNull(node) || node.isNull();
    }
    
    /**
     * 获取非空节点
     *
     * @param source 根节点
     * @param nodeNames 节点名称
     * @return java.util.List<com.fasterxml.jackson.databind.JsonNode>
     * @author huangchengxing
     * @date 2022/3/2 11:15
     */
    @NonNull
    public static List<JsonNode> findNodes(JsonNode source, Collection<String> nodeNames) {
        if (CollectionUtils.isEmpty(nodeNames)) {
            return Collections.emptyList();
        }
        return nodeNames.stream()
            .filter(StringUtils::hasText)
            .map(source::get)
            .filter(JacksonUtils::isNotNull)
            .collect(Collectors.toList());
    }

    /**
     * 获取节点
     *
     * @param source 根节点
     * @param nodeName 节点名称
     * @return com.fasterxml.jackson.databind.JsonNode 目标节点，若节点不存在或节点为null节点时返回null
     * @author huangchengxing
     * @date 2022/3/2 11:15
     */
    @Nullable
    public static JsonNode findNode(JsonNode source, String nodeName) {
        if (!StringUtils.hasText(nodeName)) {
            return null;
        }
        JsonNode node = source.get(nodeName);
        return Objects.isNull(node) || node.isNull() ? null : node;
    }

}