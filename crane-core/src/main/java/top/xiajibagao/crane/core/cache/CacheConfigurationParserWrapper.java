package top.xiajibagao.crane.core.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import top.xiajibagao.crane.core.parser.interfaces.OperateConfigurationParser;
import top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration;

import javax.annotation.Nonnull;
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
    @Nonnull
    @Override
    public T parse(@NonNull Class<?> targetClass) {
        Objects.requireNonNull(targetClass);
        return (T) configurationCache.getOrCached(getNamespace(), targetClass, configurationParser::parse);
    }

    
    
}
