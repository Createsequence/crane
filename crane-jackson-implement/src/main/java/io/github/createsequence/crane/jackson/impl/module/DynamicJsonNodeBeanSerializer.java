package io.github.createsequence.crane.jackson.impl.module;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.createsequence.crane.core.executor.OperationExecutor;
import io.github.createsequence.crane.core.helper.CollUtils;
import io.github.createsequence.crane.core.parser.interfaces.OperationConfiguration;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

/**
 * 基于操作配置允许动态添加/替换字段与字段值的序列化器
 *
 * @author huangchengxing
 * @date 2022/04/12 17:59
 */
public class DynamicJsonNodeBeanSerializer<T> extends StdSerializer<T> {

    private final ObjectMapper objectMapper;
    private final transient OperationConfiguration operationConfiguration;
    private final transient OperationExecutor operationExecutor;
    private final Set<Class<?>> groups;

    public DynamicJsonNodeBeanSerializer(
        Class<T> t, ObjectMapper objectMapper, Set<Class<?>> groups,
        OperationConfiguration operationConfiguration, OperationExecutor operationExecutor) {
        super(t);
        this.operationConfiguration = operationConfiguration;
        this.groups = groups;
        this.objectMapper = objectMapper;
        this.operationExecutor = operationExecutor;
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (Objects.isNull(value)) {
            return;
        }
        JsonNode jsonNode = objectMapper.valueToTree(value);
        operationExecutor.execute(CollUtils.adaptToCollection(jsonNode), operationConfiguration, groups);
        objectMapper.writeTree(gen, jsonNode);
    }

}
