package io.github.createsequence.crane.core.operator;

import io.github.createsequence.crane.core.handler.*;
import io.github.createsequence.crane.core.operator.interfaces.OperateProcessor;
import io.github.createsequence.crane.core.operator.interfaces.OperateProcessorComponent;

/**
 * 基于bean反射的{@link OperateProcessor}实现
 *
 * @author huangchengxing
 * @date 2022/06/27 15:30
 * @since 0.5.8
 * @see ArrayOperateHandler
 * @see CollectionOperateHandler
 * @see MapOperateHandler
 * @see NullOperateHandler
 * @see BeanOperateHandler
 */
public class BeanReflexOperateProcessor extends AbstractOperateProcessor<BeanReflexOperateProcessor> implements OperateProcessor {

    public BeanReflexOperateProcessor() {
        super(OperateProcessorComponent.OPERATE_GROUP_JAVA_BEAN);
    }

}
