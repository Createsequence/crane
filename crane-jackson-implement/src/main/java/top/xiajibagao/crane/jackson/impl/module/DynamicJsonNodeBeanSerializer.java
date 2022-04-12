package top.xiajibagao.crane.jackson.impl.module;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import top.xiajibagao.crane.core.executor.OperationExecutor;
import top.xiajibagao.crane.core.helper.CollUtils;
import top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration;

import java.io.IOException;
import java.util.Objects;

/**
 * 基于操作配置允许动态添加/替换字段与字段值的序列化器
 *
 * @author huangchengxing
 * @date 2022/04/12 17:59
 */
public class DynamicJsonNodeBeanSerializer<T> extends StdSerializer<T> {

    private final ObjectMapper objectMapper;
    private final OperationConfiguration operationConfiguration;
    private final OperationExecutor operationExecutor;

    public DynamicJsonNodeBeanSerializer(
        Class<T> t, ObjectMapper objectMapper,
        OperationConfiguration operationConfiguration, OperationExecutor operationExecutor) {
        super(t);
        this.operationConfiguration = operationConfiguration;
        this.objectMapper = objectMapper;
        this.operationExecutor = operationExecutor;
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (Objects.isNull(value)) {
            return;
        }
        JsonNode jsonNode = objectMapper.valueToTree(value);
        operationExecutor.execute(CollUtils.adaptToCollection(value), operationConfiguration);
        objectMapper.writeTree(gen, jsonNode);
    }

}
