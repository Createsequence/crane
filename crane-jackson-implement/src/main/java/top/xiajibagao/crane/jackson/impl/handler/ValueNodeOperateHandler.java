package top.xiajibagao.crane.jackson.impl.handler;

import cn.hutool.core.util.ClassUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ValueNode;
import top.xiajibagao.crane.core.annotation.GroupRegister;
import top.xiajibagao.crane.core.operator.interfaces.OperateProcessor;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.Operation;
import top.xiajibagao.crane.core.parser.interfaces.PropertyMapping;

import java.util.Objects;

/**
 * {@link ValueNode}节点处理器
 *
 * @since 0.2.0
 * @author huangchengxing
 * @date 2022/04/12 13:07
 */
@GroupRegister(OperateProcessor.OPERATE_GROUP_JSON_BEAN)
public class ValueNodeOperateHandler extends AbstractJacksonNodeOperateHandler {

    public ValueNodeOperateHandler(ObjectMapper objectMapper, OperateProcessor operateProcessor) {
        super(objectMapper, operateProcessor);
    }

    @Override
    public boolean sourceCanRead(Object source, PropertyMapping property, Operation operation) {
        if (Objects.isNull(source)) {
            return false;
        }
        return source instanceof ValueNode || ClassUtil.isBasicType(source.getClass()) || source instanceof String;
    }

    @Override
    public JsonNode readFromSource(Object source, PropertyMapping property, Operation operation) {
        // 值节点总是返回他本身
        return objectMapper.valueToTree(source);
    }

    @Override
    public boolean targetCanWrite(Object sourceData, Object target, PropertyMapping property, AssembleOperation operation) {
        return target instanceof ValueNode;
    }

    @Override
    public void writeToTarget(Object sourceData, Object target, PropertyMapping property, AssembleOperation operation) {
        // 值节点无法做任何处理
    }

}
