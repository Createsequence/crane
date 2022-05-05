package top.xiajibagao.crane.core.cache;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import top.xiajibagao.crane.core.operator.interfaces.OperatorFactory;
import top.xiajibagao.crane.core.parser.interfaces.OperationConfiguration;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author huangchengxing
 * @date 2022/4/5 21:03
 */
public class OperationConfigurationCache implements ConfigurationCache {
	
	private final Map<String, Table<Class<? extends OperatorFactory>, Class<?>, OperationConfiguration>> parsedConfigurationCaches;
	private final Supplier<Table<Class<? extends OperatorFactory>, Class<?>, OperationConfiguration>> cacheFactory;
	
	public OperationConfigurationCache(Supplier<Table<Class<? extends OperatorFactory>, Class<?>, OperationConfiguration>> cacheFactory) {
		this.parsedConfigurationCaches = new ConcurrentHashMap<>();
		this.cacheFactory = cacheFactory;
	}
	
	public OperationConfigurationCache() {
		this(() -> Tables.synchronizedTable(HashBasedTable.create()));
	}

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
	@Override
	public boolean setConfigurationCache(
			String namespace, Class<? extends OperatorFactory> factoryType, Class<?> targetType, OperationConfiguration configuration) {
		Table<Class<? extends OperatorFactory>, Class<?>, OperationConfiguration> cache = parsedConfigurationCaches.computeIfAbsent(
				namespace, ns -> cacheFactory.get()
		);
		if (!cache.contains(factoryType, targetType)) {
			synchronized (this) {
				if (!cache.contains(factoryType, targetType)) {
					cache.put(factoryType, targetType, configuration);
					return true;
				}
			}
		}
		return false;
	}

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
	@Override
	public OperationConfiguration getCachedConfiguration(
			String namespace, Class<? extends OperatorFactory> factoryType, Class<?> targetType) {
		return Optional.ofNullable(parsedConfigurationCaches.get(namespace))
				.map(cache -> cache.get(factoryType, targetType))
				.orElse(null);
	}
	
}
