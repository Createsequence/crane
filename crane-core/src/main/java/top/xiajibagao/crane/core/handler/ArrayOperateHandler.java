package top.xiajibagao.crane.core.handler;

import cn.hutool.core.util.ArrayUtil;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandler;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandlerChain;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.Operation;
import top.xiajibagao.crane.core.parser.interfaces.PropertyMapping;

import java.util.Arrays;
import java.util.Objects;

/**
 * 处理数组类型的数据源与待处理对象
 *
 * @since 0.2.0
 * @author huangchengxing
 * @date 2022/04/08 16:39
 */
public class ArrayOperateHandler extends CollectionOperateHandler implements OperateHandler {

    public ArrayOperateHandler(OperateHandlerChain handlerChain) {
        super(handlerChain);
    }

    @Override
    public boolean sourceCanRead(Object source, PropertyMapping property, Operation operation) {
        return Objects.nonNull(source) && source.getClass().isArray();
    }

    @Override
    public Object readFromSource(Object source, PropertyMapping property, Operation operation) {
        if (ArrayUtil.isEmpty(source)) {
            return null;
        }
        return super.readFromSource(Arrays.asList((Object[])source), property, operation);
    }

    @Override
    public boolean targetCanWrite(Object sourceData, Object target, PropertyMapping property, AssembleOperation operation) {
        return Objects.nonNull(target) && target.getClass().isArray();
    }

    @Override
    public void writeToTarget(Object sourceData, Object target, PropertyMapping property, AssembleOperation operation) {
        if (ArrayUtil.isNotEmpty(target)) {
            super.writeToTarget(sourceData, Arrays.asList((Object[])target), property, operation);
        }
    }

}
