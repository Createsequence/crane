package io.github.createsequence.crane.core.handler;

import io.github.createsequence.crane.core.annotation.ProcessorComponent;
import io.github.createsequence.crane.core.handler.interfaces.OperateHandler;
import io.github.createsequence.crane.core.operator.interfaces.OperateProcessor;
import io.github.createsequence.crane.core.operator.interfaces.OperateProcessorComponent;
import io.github.createsequence.crane.core.parser.interfaces.AssembleOperation;
import io.github.createsequence.crane.core.parser.interfaces.Operation;
import io.github.createsequence.crane.core.parser.interfaces.PropertyMapping;
import org.springframework.core.Ordered;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * 用于处理null值的节点，一般当具有最高的优先级，当待读取的数据源或待写入的待处理对象为null时，
 * 优先使用该节点处理，避免后续节点被无意义的反复调用
 *
 * @author huangchengxing
 * @date 2022/06/07 16:49
 * @since 0.5.5
 */
@ProcessorComponent(OperateProcessorComponent.OPERATE_GROUP_JAVA_BEAN)
public class NullOperateHandler extends AbstractOperateHandler implements OperateHandler {

    public NullOperateHandler(OperateProcessor operateProcessor, String... defaultRegisterGroups) {
        super(operateProcessor, defaultRegisterGroups);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public boolean sourceCanRead(@Nullable Object source, PropertyMapping property, Operation operation) {
        return Objects.isNull(source);
    }

    @Override
    public Object readFromSource(@Nullable Object source, PropertyMapping property, Operation operation) {
        // 直接返回null
        return null;
    }

    @Override
    public boolean targetCanWrite(@Nullable Object sourceData, @Nullable Object target, PropertyMapping property, AssembleOperation operation) {
        return Objects.isNull(target);
    }

    @Override
    public void writeToTarget(@Nullable Object sourceData, @Nullable Object target, PropertyMapping property, AssembleOperation operation) {
        // 不做任何处理
    }

}
