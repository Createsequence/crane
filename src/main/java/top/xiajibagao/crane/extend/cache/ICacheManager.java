package top.xiajibagao.crane.extend.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Objects;

/**
 * 缓存管理器
 *
 * @author huangchengxing
 * @date 2022/03/24 11:51
 */
// TODO 全局的各项缓存是否可以改进为默认使用相同的Manager同一管理？
public interface ICacheManager extends CacheManager {

    /**
     * 开辟缓存
     *
     * @param names 缓存名称
     * @author huangchengxing
     * @date 2022/3/24 12:22
     */
    Cache createCache(String names);

    /**
     * 获取缓存，若不存在该缓存，则根据缓存名称创建并添加新的缓存
     *
     * @param name 缓存名称
     * @return org.springframework.cache.Cache
     * @author huangchengxing
     * @date 2022/3/24 12:22
     */
    default Cache createIfAbsent(String name) {
        Cache cache = getCache(name);
        if (Objects.isNull(cache)) {
            cache = createCache(name);
        }
        return cache;
    }

}
