package top.xiajibagao.crane.jackson.impl.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import org.springframework.core.Ordered;
import top.xiajibagao.crane.core.annotation.ProcessorComponent;
import top.xiajibagao.crane.core.operator.interfaces.OperateProcessor;
import top.xiajibagao.crane.core.operator.interfaces.OperateProcessorComponent;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.Operation;
import top.xiajibagao.crane.core.parser.interfaces.PropertyMapping;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * 用于处理null值或{@link NullNode}节点，具有最高的优先级。
 * 当待读取的数据源或待写入的待处理对象为null时，优先使用该节点处理，
 * 避免后续节点被无意义的反复调用。
 *
 * @author huangchengxing
 * @date 2022/06/07 17:08
 */
@ProcessorComponent(OperateProcessorComponent.OPERATE_GROUP_JSON_BEAN)
public class NullNodeOperateHandler extends AbstractJacksonNodeOperateHandler {

    public NullNodeOperateHandler(ObjectMapper objectMapper, OperateProcessor operateProcessor, String... defaultRegisterGroups) {
        super(objectMapper, operateProcessor, defaultRegisterGroups);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public boolean sourceCanRead(@Nullable Object source, PropertyMapping property, Operation operation) {
        return Objects.isNull(source) || source instanceof NullNode;
    }

    @Override
    public NullNode readFromSource(@Nullable Object source, PropertyMapping property, Operation operation) {
        // 直接返回null
        return NullNode.getInstance();
    }

    @Override
    public boolean targetCanWrite(@Nullable Object sourceData, @Nullable Object target, PropertyMapping property, AssembleOperation operation) {
        return Objects.isNull(target) || target instanceof NullNode;
    }

    @Override
    public void writeToTarget(@Nullable Object sourceData, @Nullable Object target, PropertyMapping property, AssembleOperation operation) {
        // 不做任何处理
    }
}
