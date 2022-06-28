package top.xiajibagao.crane.jackson.impl.operator;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import top.xiajibagao.crane.core.operator.AbstractOperateProcessor;
import top.xiajibagao.crane.core.operator.interfaces.OperateProcessor;
import top.xiajibagao.crane.core.operator.interfaces.OperateProcessorComponent;
import top.xiajibagao.crane.jackson.impl.handler.ArrayNodeOperateHandler;
import top.xiajibagao.crane.jackson.impl.handler.NullNodeOperateHandler;
import top.xiajibagao.crane.jackson.impl.handler.ObjectNodeOperateHandler;
import top.xiajibagao.crane.jackson.impl.handler.ValueNodeOperateHandler;

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
