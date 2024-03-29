package io.github.createsequence.crane.starter.core.cache;

import io.github.createsequence.crane.core.cache.CacheConfigurationParserWrapper;
import io.github.createsequence.crane.core.cache.ConfigurationCache;
import io.github.createsequence.crane.core.cache.OperationConfigurationCache;
import io.github.createsequence.crane.core.parser.FieldAnnotationConfigurationParser;
import io.github.createsequence.crane.core.parser.interfaces.GlobalConfiguration;
import io.github.createsequence.crane.core.parser.interfaces.OperateConfigurationParser;
import io.github.createsequence.crane.core.parser.interfaces.OperationConfiguration;
import io.github.createsequence.crane.starter.core.CoreTestConfig;
import lombok.Data;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author huangchengxing
 * @date 2022/06/01 11:11
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CoreTestConfig.class)
public class CacheConfigurationParserWrapperTest {

    @Autowired
    GlobalConfiguration globalConfiguration;
    @Autowired
    BeanFactory beanFactory;

    @Test
    public void testCacheConfigurationParserWrapper() {
        ConfigurationCache configurationCache = new OperationConfigurationCache();
        OperateConfigurationParser fieldAnnotationConfigurationParser = new FieldAnnotationConfigurationParser(globalConfiguration, beanFactory);
        String cacheName = fieldAnnotationConfigurationParser.getClass().getName();
        Assertions.assertNull(configurationCache.getCachedConfiguration(cacheName, Example.class));

        OperateConfigurationParser wrapperParser = new CacheConfigurationParserWrapper(configurationCache, fieldAnnotationConfigurationParser);
        OperationConfiguration configuration = wrapperParser.parse(Example.class);
        Assertions.assertEquals(configuration, configurationCache.getCachedConfiguration(cacheName, Example.class));
        OperationConfiguration cachedConfiguration = wrapperParser.parse(Example.class);
        Assertions.assertEquals(configuration, cachedConfiguration);
    }

    @Data
    private static class Example {

    }

}
