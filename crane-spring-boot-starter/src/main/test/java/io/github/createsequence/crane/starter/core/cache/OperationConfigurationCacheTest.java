package io.github.createsequence.crane.starter.core.cache;

import io.github.createsequence.crane.core.cache.ConfigurationCache;
import io.github.createsequence.crane.core.cache.OperationConfigurationCache;
import io.github.createsequence.crane.core.parser.ClassAnnotationConfigurationParser;
import io.github.createsequence.crane.core.parser.FieldAnnotationConfigurationParser;
import io.github.createsequence.crane.core.parser.interfaces.OperationConfiguration;
import io.github.createsequence.crane.starter.core.CoreTestConfig;
import lombok.Data;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author huangchengxing
 * @date 2022/06/01 10:58
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CoreTestConfig.class)
public class OperationConfigurationCacheTest {

    @Autowired
    FieldAnnotationConfigurationParser fieldAnnotationConfigurationParser;
    @Autowired
    ClassAnnotationConfigurationParser classAnnotationConfigurationParser;

    @Test
    public void testOperationConfigurationCache() {
        ConfigurationCache configurationCache = new OperationConfigurationCache();

        // 测试手动添加缓存
        String cacheName = fieldAnnotationConfigurationParser.getClass().toString();
        Assertions.assertNull(configurationCache.getCachedConfiguration(cacheName, Example.class));
        OperationConfiguration configuration = fieldAnnotationConfigurationParser.parse(Example.class);
        configurationCache.setConfigurationCache(cacheName, Example.class, configuration);
        OperationConfiguration cachedConfiguration = configurationCache.getCachedConfiguration(cacheName, Example.class);
        Assertions.assertEquals(configuration, cachedConfiguration);
        Assertions.assertEquals(configuration, configurationCache.getOrCached(cacheName, Example.class, fieldAnnotationConfigurationParser::parse));

        cacheName = classAnnotationConfigurationParser.getClass().getName();
        Assertions.assertNull(configurationCache.getCachedConfiguration(cacheName, Example.class));
        configuration = configurationCache.getOrCached(cacheName, Example.class, classAnnotationConfigurationParser::parse);
        Assertions.assertNotNull(configurationCache.getCachedConfiguration(cacheName, Example.class));
        Assertions.assertEquals(configuration, configurationCache.getCachedConfiguration(cacheName, Example.class));
    }

    @Data
    private static class Example {

    }

}
