package top.xiajibagao.crane.core.helper;

import cn.hutool.core.collection.CollUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.BeanFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * {@link RespChainExecutor}的简单实现
 *
 * @author huangchengxing
 * @date 2022/04/06 12:12
 */
@Accessors(fluent = true)
@Getter
@RequiredArgsConstructor
public class SimpleRespChainExecutor<C> implements RespChainExecutor<C> {

    private final List<RespChainHandler<C>> handlers = new ArrayList<>();
    private final BeanFactory beanFactory;

    public SimpleRespChainExecutor<C> addHandler(RespChainHandler<C> handler) {
        Objects.requireNonNull(handler);
        handlers.add(handler);
        return this;
    }

    public SimpleRespChainExecutor<C> addHandlers(Collection<RespChainHandler<C>> handlers) {
        if (CollUtil.isNotEmpty(handlers)) {
            handlers.forEach(this::addHandler);
        }
        return this;
    }

    public SimpleRespChainExecutor<C> addHandler(Class<? extends RespChainHandler<C>> handler) {
        Objects.requireNonNull(handler);
        handlers.add(beanFactory.getBean(handler));
        return this;
    }

    public SimpleRespChainExecutor<C> addHandler(Collection<Class<? extends RespChainHandler<C>>> handlers) {
        if (CollUtil.isNotEmpty(handlers)) {
            handlers.forEach(this::addHandler);
        }
        return this;
    }


}
