package top.xiajibagao.crane.extend.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import top.xiajibagao.crane.operator.interfaces.OperatorFactory;
import top.xiajibagao.crane.parse.interfaces.OperateConfigurationParser;
import top.xiajibagao.crane.parse.interfaces.OperationConfiguration;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * {@link OperateConfigurationParser}包装类，基于{@link ICacheManager}为包装的解析器提供解析配置缓存功能
 *
 * @author huangchengxing
 * @date 2022/03/24 11:32
 */
@RequiredArgsConstructor
public class CacheableParserWrapper<T extends OperationConfiguration> implements OperateConfigurationParser<T> {

    private final ICacheManager cacheManager;
    private final OperateConfigurationParser<T> configurationParser;

    @SuppressWarnings("unchecked")
    @Override
    public T parse(@NotNull Class<?> targetClass, @NotNull OperatorFactory operatorFactory) {
        Objects.requireNonNull(targetClass);
        Objects.requireNonNull(operatorFactory);
        String operatorFactoryName = operatorFactory.getClass().getName();
        Cache cache = cacheManager.createIfAbsent(operatorFactoryName);

        T configuration = (T) cache.get(targetClass, OperationConfiguration.class);
        if (Objects.isNull(configuration)) {
            configuration = configurationParser.parse(targetClass, operatorFactory);
            cache.put(targetClass, configuration);
        }
        cache.putIfAbsent(targetClass, configurationParser.parse(targetClass, operatorFactory));
        return configuration;
    }

}
