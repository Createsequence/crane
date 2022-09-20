package io.github.createsequence.crane.core.cache;

import io.github.createsequence.crane.core.parser.interfaces.OperateConfigurationParser;
import io.github.createsequence.crane.core.parser.interfaces.OperationConfiguration;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;

/**
 * 配置缓存
 *
 * <p>表明一个{@link OperationConfiguration}的缓存。实现类应该提供一个类似
 * {@code CacheName -> TargetType -> OperationConfiguration}的二级索引结构。<br />
 * 其中，CacheName应当用于区分配置解析器{@link OperateConfigurationParser}，比如，
 * 当针对某个TargetType调用解析器{@link OperateConfigurationParser#parse(Class)}
 * 获得的配置各项属性都完全一致时，应当认为这些都属于同一个CacheName。<br />
 * 在默认情况下，CacheName应当与解析器的{@link Class#getName()}方法返回值保持一致。
 *
 * <p>接口提供{@link #getOrCached(String, Class, Function)}方法用于提供"getOrCreate"的操作，
 * 但是该操作存在潜在的线程不安全的风险，推荐实现类自行实现以尽可能的保证其安全性。
 *
 * @author huangchengxing
 * @date 2022/4/5 21:34
 * @see OperationConfigurationCache
 * @see OperateConfigurationParser
 */
public interface ConfigurationCache {

	/**
	 * 若缓存不存在，则将配置添加至缓存
	 *
	 * @param cacheName 缓存名称
	 * @param targetType 解析对象类型
	 * @param configuration 缓存配置
	 * @author huangchengxing
	 * @date 2022/4/5 21:21
	 */
	void setConfigurationCache(String cacheName, Class<?> targetType, OperationConfiguration configuration);
	
	/**
	 * 获取缓存的配置
	 *
	 * @param cacheName 缓存名称
	 * @param targetType 解析对象类型
	 * @return interfaces.parser.io.github.createsequence.crane.core.OperationConfiguration
	 * @author huangchengxing
	 * @date 2022/4/5 21:24
	 */
	@Nullable
	OperationConfiguration getCachedConfiguration(String cacheName, Class<?> targetType);
	
	/**
	 * 获取缓存，若不存则先获取配置并缓存
	 *
	 * @param cacheName 缓存名称
	 * @param targetType 解析对象类型
	 * @param configurationFactory 配置的获取方法
	 * @return interfaces.parser.io.github.createsequence.crane.core.OperationConfiguration
	 * @author huangchengxing
	 * @date 2022/4/5 21:32
	 */
	default OperationConfiguration getOrCached(String cacheName, Class<?> targetType, Function<Class<?>, OperationConfiguration> configurationFactory) {
		OperationConfiguration configuration = getCachedConfiguration(cacheName, targetType);
		return Objects.isNull(configuration) ? configurationFactory.apply(targetType) : configuration;
	}
	
}
