package top.xiajibagao.crane.core.cache;

import top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration;

import java.util.function.Function;

/**
 * 配置缓存
 *
 * @author huangchengxing
 * @date 2022/4/5 21:34
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
	 * @return top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration
	 * @author huangchengxing
	 * @date 2022/4/5 21:24
	 */
	OperationConfiguration getCachedConfiguration(String cacheName, Class<?> targetType);
	
	/**
	 * 获取缓存，若不存则先获取配置并缓存
	 *
	 * @param cacheName 缓存名称
	 * @param targetType 解析对象类型
	 * @param configurationFactory 配置的获取方法
	 * @return top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration
	 * @author huangchengxing
	 * @date 2022/4/5 21:32
	 */
	OperationConfiguration getOrCached(String cacheName, Class<?> targetType, Function<Class<?>, OperationConfiguration> configurationFactory);
	
}
