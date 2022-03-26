package top.xiajibagao.crane.extend.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.Collections;

/**
 * 基于{@link ConcurrentMapCacheManager}的缓存实现
 *
 * @author huangchengxing
 * @date 2022/03/24 21:01
 */
public class SimpleCacheManager extends ConcurrentMapCacheManager implements ICacheManager {

    @Override
    public Cache createCache(String name) {
        setCacheNames(Collections.singletonList(name));
        return getCache(name);
    }

}
