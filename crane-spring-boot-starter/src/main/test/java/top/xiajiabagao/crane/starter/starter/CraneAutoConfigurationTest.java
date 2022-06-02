package top.xiajiabagao.crane.starter.starter;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ClassUtil;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import top.xiajiabagao.crane.starter.starter.entity.Example;
import top.xiajiabagao.crane.starter.starter.enums.TestEnum;
import top.xiajiabagao.crane.starter.starter.enums.TestEnum2;
import top.xiajibagao.crane.core.cache.ConfigurationCache;
import top.xiajibagao.crane.core.helper.EnumDict;
import top.xiajibagao.crane.core.parser.interfaces.OperateConfigurationParser;
import top.xiajibagao.crane.starter.CraneAutoConfigurationProperties;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author huangchengxing
 * @date 2022/06/02 8:18
 */
@TestPropertySource("classpath:application.yml")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {StarterTestConfig.class})
public class CraneAutoConfigurationTest {

    @Autowired
    private CraneAutoConfigurationProperties craneAutoConfigurationProperties;
    @Autowired
    private EnumDict enumDict;
    @Autowired
    private BeanFactory beanFactory;

    @Autowired
    private ConfigurationCache configurationCache;

    @Test
    public void testCraneAutoConfiguration() {
        Assertions.assertFalse(craneAutoConfigurationProperties.isEnableAsmReflect());

        // 测试枚举配置
        CraneAutoConfigurationProperties.EnumConfigProperties enumConfig = craneAutoConfigurationProperties.getEnums();
        Assertions.assertEquals(Collections.singleton("top.xiajiabagao.crane.starter.starter.enums"), enumConfig.getDictEnumPackages());
        EnumDict.EnumDictType<TestEnum> enumEnumDictType = enumDict.getType(TestEnum.class);
        Assertions.assertNotNull(enumEnumDictType);
        Assertions.assertEquals(Arrays.asList("1", "2"), CollStreamUtil.toList(enumEnumDictType.getNameCache().values(), EnumDict.EnumDictItem::getName));
        EnumDict.EnumDictType<TestEnum2> enumEnumDictType2 = enumDict.getType(TestEnum2.class);
        Assertions.assertNotNull(enumEnumDictType2);
        Assertions.assertEquals(Arrays.asList("ITEM_B", "ITEM_A"), CollStreamUtil.toList(enumEnumDictType2.getNameCache().values(), EnumDict.EnumDictItem::getName));

        // 测试预解析配置
        CraneAutoConfigurationProperties.CacheConfigProperties cacheConfig = craneAutoConfigurationProperties.getCache();
        Assertions.assertEquals(CollUtil.newHashSet("top.xiajiabagao.crane.starter.starter.entity"), cacheConfig.getPreParsedClassPackages());
        OperateConfigurationParser operateConfigurationParser = beanFactory.getBean(OperateConfigurationParser.class);
        Assertions.assertNotNull(configurationCache.getCachedConfiguration(operateConfigurationParser.getClass().getName(), Example.class));
        Assertions.assertEquals(2, cacheConfig.getParserAndPreParsedClassPackages().size());
        Assertions.assertEquals(CollUtil.newHashSet("DefaultCraneFieldAnnotationConfigurationParser", "DefaultCraneClassAnnotationConfigurationParser"), cacheConfig.getParserAndPreParsedClassPackages().keySet());
        cacheConfig.getParserAndPreParsedClassPackages().forEach((parserName, packagePaths) -> {
            OperateConfigurationParser targetParser = beanFactory.getBean(parserName, OperateConfigurationParser.class);
            packagePaths.forEach(path ->
                ClassUtil.scanPackage(path).forEach(targetClass ->
                    Assertions.assertNotNull(configurationCache.getCachedConfiguration(targetParser.getClass().getName(), targetClass)))
            );
        });
    }

}
