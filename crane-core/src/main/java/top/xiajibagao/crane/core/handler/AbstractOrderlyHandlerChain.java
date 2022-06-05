package top.xiajibagao.crane.core.handler;

import lombok.Getter;
import lombok.experimental.Accessors;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandler;
import top.xiajibagao.crane.core.handler.interfaces.OperateHandlerChain;
import top.xiajibagao.crane.core.handler.interfaces.SourceOperateInterceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * 按{@link OperateHandler#getOrder()}排序的装配器链
 *
 * @since 0.2.0
 * @author huangchengxing
 * @date 2022/04/08 21:02
 */
@Getter
@Accessors(fluent = true)
public class AbstractOrderlyHandlerChain implements OperateHandlerChain {

    protected final List<OperateHandler> handlers = new ArrayList<>();
    protected final List<SourceOperateInterceptor> interceptors = new ArrayList<>();

    @Override
    public OperateHandlerChain addInterceptor(SourceOperateInterceptor interceptor) {
        interceptors.add(interceptor);
        interceptors.sort(SourceOperateInterceptor::compareTo);
        return this;
    }

    @Override
    public OperateHandlerChain addHandler(OperateHandler handler) {
        handlers.add(handler);
        handlers.sort(OperateHandler::compareTo);
        return this;
    }
}
