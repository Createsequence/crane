package top.xiajibagao.crane.jackson.impl.handler;

import com.fasterxml.jackson.databind.JsonNode;
import top.xiajibagao.crane.core.handler.AbstractOrderlyHandlerChain;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandlerChain;
import top.xiajibagao.crane.jackson.impl.operator.JacksonAssembler;
import top.xiajibagao.crane.jackson.impl.operator.JacksonDisassembler;

/**
 * {@link JsonNode}处理器链
 *
 * @author huangchengxing
 * @date 2022/05/28 17:16
 * @since 0.5.3
 * @see JacksonAssembler
 * @see JacksonDisassembler
 */
public class JacksonOperateHandlerChain extends AbstractOrderlyHandlerChain implements OperateHandlerChain {
}
