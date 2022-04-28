package top.xiajibagao.crane.core.operator;

import cn.hutool.core.collection.CollUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandlerChain;
import top.xiajibagao.crane.core.operator.interfaces.Assembler;
import top.xiajibagao.crane.core.parser.BeanAssembleProperty;
import top.xiajibagao.crane.core.parser.EmptyAssembleProperty;
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
        List<AssembleProperty> properties = CollUtil.defaultIfEmpty(
            operation.getProperties(), Collections.singletonList(EmptyAssembleProperty.instance())
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
        return handlerChain.readFromSource(
            target, new BeanAssembleProperty(null, operation.getTargetProperty().getName(), "", Void.class),
            operation
        );
    }
    
}
