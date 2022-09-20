package io.github.createsequence.crane.jackson.impl.operator;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.createsequence.crane.core.operator.AbstractOperateProcessor;
import io.github.createsequence.crane.core.operator.interfaces.OperateProcessor;
import io.github.createsequence.crane.core.operator.interfaces.OperateProcessorComponent;
import io.github.createsequence.crane.jackson.impl.handler.ArrayNodeOperateHandler;
import io.github.createsequence.crane.jackson.impl.handler.NullNodeOperateHandler;
import io.github.createsequence.crane.jackson.impl.handler.ObjectNodeOperateHandler;
import io.github.createsequence.crane.jackson.impl.handler.ValueNodeOperateHandler;
import lombok.Getter;

/**
 * 用于处理{@link JsonNode}类型数据的操作处理器
 *
 * @author huangchengxing
 * @date 2022/06/27 16:11
 * @since 0.5.8
 * @see ArrayNodeOperateHandler
 * @see NullNodeOperateHandler
 * @see ObjectNodeOperateHandler
 * @see ValueNodeOperateHandler
 */
@Getter
public class JacksonOperateProcessor extends AbstractOperateProcessor<JacksonOperateProcessor> implements OperateProcessor {

    public JacksonOperateProcessor() {
        super(OperateProcessorComponent.OPERATE_GROUP_JSON_BEAN);
    }

}
