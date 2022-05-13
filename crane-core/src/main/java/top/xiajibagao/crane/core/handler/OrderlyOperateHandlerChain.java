package top.xiajibagao.crane.core.handler;

import lombok.Getter;
import lombok.experimental.Accessors;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandler;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandlerChain;

import java.util.ArrayList;
import java.util.List;

/**
 * 按{@link OperateHandler#getOrder()}排序的装配器链
 *
 * @author huangchengxing
 * @date 2022/04/08 21:02
 */
@Getter
@Accessors(fluent = true)
public class OrderlyOperateHandlerChain implements OperateHandlerChain {

    private final List<OperateHandler> handlers = new ArrayList<>();

    @Override
    public OperateHandlerChain addHandler(OperateHandler handler) {
        handlers.add(handler);
        handlers.sort(OperateHandler::compareTo);
        return this;
    }

}
