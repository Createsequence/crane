package top.xiajibagao.crane.core.handler;

import org.springframework.core.Ordered;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandler;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.Operation;
import top.xiajibagao.crane.core.parser.interfaces.PropertyMapping;

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
public class NullOperateHandler implements OperateHandler {

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
