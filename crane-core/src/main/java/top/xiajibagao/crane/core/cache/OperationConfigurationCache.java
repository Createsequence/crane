package top.xiajibagao.crane.core.cache;

import lombok.RequiredArgsConstructor;
import top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @author huangchengxing
 * @date 2022/4/5 21:03
 */
@RequiredArgsConstructor
public class OperationConfigurationCache implements ConfigurationCache {

	private final ConcurrentMap<String, ConcurrentMap<Class<?>, OperationConfiguration>> configurationCache = new ConcurrentHashMap<>(4);

	@Override
	public void setConfigurationCache(String cacheName, Class<?> targetType, OperationConfiguration configuration) {
		ConcurrentMap<Class<?>, OperationConfiguration> cacheMap = configurationCache.computeIfAbsent(cacheName, ns -> new ConcurrentHashMap<>(32));
		cacheMap.put(targetType, configuration);
	}

	@Nullable
	@Override
	public OperationConfiguration getCachedConfiguration(String cacheName, Class<?> targetType) {
		return Optional.ofNullable(configurationCache.get(cacheName))
			.map(cache -> cache.get(targetType))
			.orElse(null);
	}

	@Override
	public OperationConfiguration getOrCached(String cacheName, Class<?> targetType, Function<Class<?>, OperationConfiguration> configurationFactory) {
		ConcurrentMap<Class<?>, OperationConfiguration> cacheMap = configurationCache.computeIfAbsent(cacheName, ns -> new ConcurrentHashMap<>(32));
		return cacheMap.computeIfAbsent(targetType, configurationFactory);
	}
}
