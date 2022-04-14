package top.xiajibagao.crane.core.operator;

import cn.hutool.core.collection.CollUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.xiajibagao.crane.core.exception.CraneException;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandlerChain;
import top.xiajibagao.crane.core.operator.interfaces.Assembler;
import top.xiajibagao.crane.core.parser.BeanAssembleProperty;
import top.xiajibagao.crane.core.parser.interfaces.AssembleOperation;
import top.xiajibagao.crane.core.parser.interfaces.AssembleProperty;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author huangchengxing
 * @date 2022/03/02 13:29
 */
@Getter
@RequiredArgsConstructor
public class BeanReflexAssembler implements Assembler {

    private final OperateHandlerChain handlerChain;

    @Override
    public void execute(Object target, Object source, AssembleOperation operation) {
        if (Objects.isNull(target) || Objects.isNull(source)) {
            return;
        }
        checkType(target, operation);
        List<AssembleProperty> properties = CollUtil.defaultIfEmpty(
            operation.getProperties(), Collections.singletonList(AssembleProperty.empty())
        );
        for (AssembleProperty property : properties) {
            Object sourceData = handlerChain.readFromSource(source, property, operation);
            if (Objects.isNull(sourceData)) {
                return;
            }
            handlerChain.writeToTarget(sourceData, target, property, operation);
        }
    }

    @Override
    public Object getKey(Object target, AssembleOperation operation) {
        checkType(target, operation);
        return handlerChain.readFromSource(
            target, new BeanAssembleProperty(null, operation.getTargetProperty().getName(), "", Void.class),
            operation
        );
    }
    
    /**
     * 检查当前正在处理的目标实例是否与操作配置中指定的类型
     *
     * @param target 目标实例
     * @param operation 操作配置
     * @author huangchengxing
     * @date 2022/3/2 16:06
     */
    private void checkType(Object target, AssembleOperation operation) {
        CraneException.throwIfFalse(
            operation.getOwner().getTargetClass().isAssignableFrom(target.getClass()),
            "操作配置类型为[{}]，但待处理数据类型为[{}]",
            operation.getOwner().getTargetClass(), target.getClass()
        );
    }

}
