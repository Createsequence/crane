package top.xiajibagao.crane.core.handler;

import cn.hutool.core.collection.CollUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 按{@link AssembleHandler#order()}排序的装配器链
 *
 * @author huangchengxing
 * @date 2022/04/08 21:02
 */
public class OrderlyAssembleHandlerChain implements AssembleHandlerChain {

    private final List<AssembleHandler> handlers = new ArrayList<>();

    @Override
    public List<AssembleHandler> handlers() {
        return CollUtil.sort(handlers, Comparator.comparing(AssembleHandler::order));
    }

    @Override
    public AssembleHandlerChain addHandler(AssembleHandler handler) {
        handlers.add(handler);
        return this;
    }

}
