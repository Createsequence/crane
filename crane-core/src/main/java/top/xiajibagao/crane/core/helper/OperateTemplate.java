package top.xiajibagao.crane.core.helper;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import top.xiajibagao.crane.core.cache.ConfigurationCache;
import top.xiajibagao.crane.core.executor.OperationExecutor;
import top.xiajibagao.crane.core.operator.interfaces.OperatorFactory;
import top.xiajibagao.crane.core.parser.interfaces.OperateConfigurationParser;
import top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration;

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
    private final OperatorFactory defaultOperatorFactory;
    private final OperateConfigurationParser<? extends OperationConfiguration> defaultOperateConfigurationParser;
    private final OperationExecutor defaultOperationExecutor;

    /**
     * 使用默认执行器与指定操作配置处理数据
     *
     * @param target 待处理对象
     * @param configuration 操作配置
     * @author huangchengxing
     * @date 2022/4/9 23:44
     */
    public void process(Object target, OperationConfiguration configuration) {
        process(target, configuration, defaultOperationExecutor);
    }

    /**
     * 根据指定执行器与操作配置处理数据
     *
     * @param target 待处理对象
     * @param configuration 指定操作配置
     * @param executor 执行器
     * @author huangchengxing
     * @date 2022/4/9 23:44
     */
    public void process(Object target,
        OperationConfiguration configuration,
        OperationExecutor executor) {
        executor.execute(CollUtils.adaptToCollection(target), configuration);
    }

    /**
     * 根据指定配置处理数据
     *
     * @param target 待处理数据
     * @param factory 操作者工厂
     * @param parser 配置解析器
     * @param executor 执行器
     * @author huangchengxing
     * @date 2022/4/9 23:43
     */
    public void process(
        Object target,
        OperatorFactory factory,
        OperateConfigurationParser<? extends OperationConfiguration> parser,
        OperationExecutor executor) {
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
            parser.getClass().getName(), factory.getClass(), targetClass, () -> parser.parse(targetClass, factory)
        );
        // 根据处理数据
        executor.execute(targets, configuration);
    }

    /**
     * 使用默认执行器，然后根据指定配置处理数据
     *
     * @param target 待处理数据
     * @param factory 操作者工厂
     * @param parser 配置解析器
     * @author huangchengxing
     * @date 2022/4/9 23:43
     */
    public void process(
        Object target,
        OperatorFactory factory,
        OperateConfigurationParser<? extends OperationConfiguration> parser) {
        process(target, factory, parser, defaultOperationExecutor);
    }

    /**
     * 使用默认执行器和配置解析器，然后根据指定配置处理数据
     *
     * @param target 待处理数据
     * @param factory 操作者工厂
     * @author huangchengxing
     * @date 2022/4/9 23:43
     */
    public void process(Object target, OperatorFactory factory) {
        process(target, factory, defaultOperateConfigurationParser, defaultOperationExecutor);
    }

    /**
     * 使用默认执行器和配置解析器，然后根据指定配置处理数据
     *
     * @param target 待处理数据
     * @param executor 执行器
     * @author huangchengxing
     * @date 2022/4/9 23:43
     */
    public void process(Object target, OperationExecutor executor) {
        process(target, defaultOperatorFactory, defaultOperateConfigurationParser, executor);
    }
    /**
     * 使用默认执行器、配置解析器与操作者工厂处理数据
     *
     * @param target 待处理数据
     * @author huangchengxing
     * @date 2022/4/9 23:43
     */
    public void process(Object target) {
        process(target, defaultOperatorFactory, defaultOperateConfigurationParser, defaultOperationExecutor);
    }

}
