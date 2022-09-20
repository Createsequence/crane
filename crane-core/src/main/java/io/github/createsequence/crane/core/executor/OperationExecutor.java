package io.github.createsequence.crane.core.executor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import io.github.createsequence.crane.core.helper.DefaultGroup;
import io.github.createsequence.crane.core.parser.interfaces.OperationConfiguration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

/**
 * 执行器
 * <p>用于根据类型操作配置，驱动并完成类中的全部装卸/装配操作
 *
 * @author huangchengxing
 * @date 2022/03/01 17:58
 * @see OperationConfiguration
 */
public interface OperationExecutor {

    /**
     * 执行操作
     *
     * @param targets 目标实例
     * @param operationConfiguration 目标类操作配置
     * @param groups 操作的组，若组为空则将不操作任何数据
     * @author huangchengxing
     * @date 2022/3/1 17:59
     */
    void execute(Iterable<?> targets, OperationConfiguration operationConfiguration, @Nonnull Set<Class<?>> groups);

    /**
     * 执行操作
     *
     * @param targets 目标实例
     * @param operationConfiguration 目标类操作配置
     * @param groups 操作的组，若组为空则默认操作{@link DefaultGroup}
     * @author huangchengxing
     * @date 2022/3/1 17:59
     */
    default void execute(Iterable<?> targets, OperationConfiguration operationConfiguration, @Nullable Class<?>... groups) {
        execute(targets, operationConfiguration, ArrayUtil.isNotEmpty(groups) ? CollUtil.newHashSet(groups) : Collections.singleton(DefaultGroup.class));
    }

}
