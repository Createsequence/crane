package top.xiajibagao.crane.core.helper;

import cn.hutool.core.collection.CollUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 责任链执行器，也视为一个特殊的责任链节点
 *
 * @param <C> 上下文类型
 * @author huangchengxing
 * @date 2022/04/06 12:59
 */
public interface RespChainExecutor<C> extends RespChainHandler<C> {

    /**
     * 获取全部节点
     *
     * @return java.util.List<top.xiajibagao.crane.core.helper.RespChainHandler<C>>
     * @author huangchengxing
     * @date 2022/4/6 12:00
     */
    List<RespChainHandler<C>> handlers();
    
    /**
     * 仅当有可执行节点时才调用
     *
     * @param context 上下文
     * @return boolean
     * @author huangchengxing
     * @date 2022/4/6 12:12:
     */
    @Override
    default boolean support(C context) {
        return CollUtil.isNotEmpty(handlers());
    }

    /**
     * 按顺序执行全部支持处理该类型上下文的节点
     *
     * @param context 上下文
     * @return boolean 是否存在节点处理后中断链执行
     * @author huangchengxing
     * @date 2022/4/6 12:07
     */
    @Override
    default boolean process(C context) {
        List<RespChainHandler<C>> handlers = CollUtil.defaultIfEmpty(handlers(), Collections.emptyList()).stream()
            .filter(handler -> handler.support(context))
            .sorted(Comparator.comparing(RespChainHandler::order))
            .collect(Collectors.toList());
        for (RespChainHandler<C> handler : handlers) {
            if (!handler.process(context)) {
                return false;
            }
        }
        return true;
    }

}
