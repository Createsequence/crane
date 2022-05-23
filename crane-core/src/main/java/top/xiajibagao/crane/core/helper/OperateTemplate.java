package top.xiajibagao.crane.core.helper;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import top.xiajibagao.crane.core.cache.ConfigurationCache;
import top.xiajibagao.crane.core.executor.OperationExecutor;
import top.xiajibagao.crane.core.parser.interfaces.OperateConfigurationParser;
import top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;

/**
 * 操作辅助类，用于一步完成配置解析、配置执行操作，从而快速完成数据处理 <br />
 * 初始化后为实例指定默认的配置缓存、操作者工厂、解析器、执行器，
 * 处理数据时，根据需求自行选择重载方法以选择哪些组件使用指定配置，其余组件皆使用默认配置。
 *
 * @author huangchengxing
 * @date 2022/04/09 23:19
 */
@RequiredArgsConstructor
public class OperateTemplate {

    private final ConfigurationCache configurationCache;
    private final OperateConfigurationParser defaultOperateConfigurationParser;
    private final OperationExecutor defaultOperationExecutor;

    /**
     * 使用默认执行器与指定操作配置处理数据
     *
     * @param target 待处理对象
     * @param configuration 操作配置
     * @param groups 要处理的指定组，为空则默认为{@link DefaultGroup}
     * @author huangchengxing
     * @date 2022/4/9 23:44
     */
    public void process(Object target, OperationConfiguration configuration, @Nullable Class<?>... groups) {
        process(target, configuration, defaultOperationExecutor, groups);
    }

    /**
     * 根据指定执行器与操作配置处理数据
     *
     * @param target 待处理对象
     * @param configuration 指定操作配置
     * @param executor 执行器
     * @param groups 要处理的指定组，为空则默认为{@link DefaultGroup}
     * @author huangchengxing
     * @date 2022/4/9 23:44
     */
    public void process(Object target, OperationConfiguration configuration, OperationExecutor executor, @Nullable Class<?>... groups) {
        executor.execute(CollUtils.adaptToCollection(target), configuration, groups);
    }

    /**
     * 根据指定配置处理数据
     *
     * @param target 待处理数据
     * @param parser 配置解析器
     * @param executor 执行器
     * @param groups 要处理的指定组，为空则默认为{@link DefaultGroup}
     * @author huangchengxing
     * @date 2022/4/9 23:43
     */
    public void process(
        Object target,
        OperateConfigurationParser parser,
        OperationExecutor executor,
        @Nullable Class<?>... groups) {

        // 适配为集合
        Collection<?> targets = CollUtils.adaptToCollection(target);
        if (CollUtil.isEmpty(targets)) {
            return;
        }
        // 获取类型
        Class<?> targetClass = ObjectUtils.computeIfNotNull(CollUtil.getFirst(targets), Object::getClass);
        if (Objects.isNull(targetClass)) {
            return;
        }
        // 解析配置
        OperationConfiguration configuration = configurationCache.getOrCached(
            parser.getClass().getName(), targetClass, parser::parse
        );
        // 根据处理数据
        executor.execute(targets, configuration, groups);
    }

    /**
     * 使用默认执行器，然后根据指定配置处理数据
     *
     * @param target 待处理数据
     * @param parser 配置解析器
     * @param groups 要处理的指定组，为空则默认为{@link DefaultGroup}
     * @author huangchengxing
     * @date 2022/4/9 23:43
     */
    public void process(Object target, OperateConfigurationParser parser, @Nullable Class<?>... groups) {
        process(target, parser, defaultOperationExecutor, groups);
    }

    /**
     * 使用默认执行器和配置解析器，然后根据指定配置处理数据
     *
     * @param target 待处理数据
     * @param executor 执行器
     * @param groups 要处理的指定组，为空则默认为{@link DefaultGroup}
     * @author huangchengxing
     * @date 2022/4/9 23:43
     */
    public void process(Object target, OperationExecutor executor, @Nullable Class<?>... groups) {
        process(target, defaultOperateConfigurationParser, executor, groups);
    }
    /**
     * 使用默认执行器、配置解析器与操作者工厂处理数据
     *
     * @param target 待处理数据
     * @param groups 要处理的指定组，为空则默认为{@link DefaultGroup}
     * @author huangchengxing
     * @date 2022/4/9 23:43
     */
    public void process(Object target, @Nullable Class<?>... groups) {
        process(target, defaultOperateConfigurationParser, defaultOperationExecutor, groups);
    }

}
