package top.xiajibagao.crane.extension.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import top.xiajibagao.crane.core.operator.interfaces.OperatorFactory;
import top.xiajibagao.crane.core.parser.interfaces.OperateConfigurationParser;
import top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration;

import java.util.Objects;

/**
 * {@link OperateConfigurationParser}包装类，基于{@link ConfigurationCache}为包装的解析器提供解析配置缓存功能
 *
 * @author huangchengxing
 * @date 2022/03/24 11:32
 */
@RequiredArgsConstructor
public class CacheConfigurationParserWrapper<T extends OperationConfiguration> implements OperateConfigurationParser<T> {

    private final ConfigurationCache configurationCache;
    private final OperateConfigurationParser<T> configurationParser;

    protected String getNamespace() {
        return configurationParser.getClass().getName();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public T parse(@NonNull Class<?> targetClass, @NonNull OperatorFactory operatorFactory) {
        Objects.requireNonNull(targetClass);
        Objects.requireNonNull(operatorFactory);
        return (T) configurationCache.getOrCached(
                getNamespace(),
                operatorFactory.getClass(),
                targetClass,
                () -> configurationParser.parse(targetClass, operatorFactory)
        );
    }

    
    
}
