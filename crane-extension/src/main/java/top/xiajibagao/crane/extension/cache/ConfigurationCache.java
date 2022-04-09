package top.xiajibagao.crane.extension.cache;

import top.xiajibagao.crane.core.operator.interfaces.OperatorFactory;
import top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration;

import java.util.Objects;
import java.util.function.Supplier;

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
	 * @param namespace 命名空间
	 * @param factoryType 操作者工厂类型
	 * @param targetType 解析对象类型
	 * @param configuration 缓存配置
	 * @return boolean 是否设置成功
	 * @author huangchengxing
	 * @date 2022/4/5 21:21
	 */
	boolean setConfigurationCache(
			String namespace,
			Class<? extends OperatorFactory> factoryType,
			Class<?> targetType,
			OperationConfiguration configuration
	);
	
	/**
	 * 获取缓存的配置
	 *
	 * @param namespace 命名空间
	 * @param factoryType 操作者工厂类型
	 * @param targetType 解析对象类型
	 * @return top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration
	 * @author huangchengxing
	 * @date 2022/4/5 21:24
	 */
	OperationConfiguration getCachedConfiguration(
			String namespace,
			Class<? extends OperatorFactory> factoryType,
			Class<?> targetType
	);
	
	/**
	 * 获取缓存，若不存则先获取配置并缓存
	 *
	 * @param namespace 命名空间
	 * @param factoryType 操作者工厂类型
	 * @param targetType 解析对象类型
	 * @param configurationFactory 配置的获取方法
	 * @return top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration
	 * @author huangchengxing
	 * @date 2022/4/5 21:32
	 */
	default OperationConfiguration getOrCached(
			String namespace,
			Class<? extends OperatorFactory> factoryType,
			Class<?> targetType,
			Supplier<OperationConfiguration> configurationFactory) {
		OperationConfiguration configuration = getCachedConfiguration(namespace, factoryType, targetType);
		if (Objects.isNull(configuration)) {
			synchronized (this) {
				configuration = getCachedConfiguration(namespace, factoryType, targetType);
				if (Objects.isNull(configuration)) {
					configuration = configurationFactory.get();
					setConfigurationCache(namespace, factoryType, targetType, configuration);
				}
			}
		}
		return configuration;
	}
	
}
