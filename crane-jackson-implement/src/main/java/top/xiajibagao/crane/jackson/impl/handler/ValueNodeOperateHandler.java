package top.xiajibagao.crane.jackson.impl.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.springframework.util.ClassUtils;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandler;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.Operation;
import top.xiajibagao.crane.core.parser.interfaces.PropertyMapping;
import top.xiajibagao.crane.jackson.impl.helper.JacksonUtils;

import java.util.Objects;

/**
 * {@link ValueNode}节点处理器
 *
 * @since 0.2.0
 * @author huangchengxing
 * @date 2022/04/12 13:07
 */
public class ValueNodeOperateHandler extends AbstractJacksonNodeOperateHandler implements OperateHandler {

    public ValueNodeOperateHandler(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public boolean sourceCanRead(Object source, PropertyMapping property, Operation operation) {
        return Objects.nonNull(source) && ClassUtils.isAssignable(ValueNode.class, source.getClass());
    }

    @Override
    public boolean targetCanWrite(Object sourceData, Object target, PropertyMapping property, AssembleOperation operation) {
        return Objects.nonNull(target) && ClassUtils.isAssignable(ValueNode.class, target.getClass());
    }

    @Override
    public Object readFromSource(Object source, PropertyMapping property, Operation operation) {
        if (Objects.isNull(source) || !(source instanceof ValueNode) || JacksonUtils.isNull((ValueNode)source)) {
            return NullNode.getInstance();
        }
        return source;
    }

    @Override
    public void writeToTarget(Object sourceData, Object target, PropertyMapping property, AssembleOperation operation) {
        // 值节点无法做任何处理
    }
}
