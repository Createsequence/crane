package top.xiajibagao.crane.core.handler;

import cn.hutool.core.collection.CollUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 按{@link OperateHandler#order()}排序的装配器链
 *
 * @author huangchengxing
 * @date 2022/04/08 21:02
 */
public class OrderlyOperateHandlerChain implements OperateHandlerChain {

    private final List<OperateHandler> handlers = new ArrayList<>();

    @Override
    public List<OperateHandler> handlers() {
        return CollUtil.sort(handlers, Comparator.comparing(OperateHandler::order));
    }

    @Override
    public OperateHandlerChain addHandler(OperateHandler handler) {
        handlers.add(handler);
        return this;
    }

}
