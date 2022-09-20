package io.github.createsequence.crane.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author huangchengxing
 * @date 2022/05/05 23:49
 */
@Configuration
@ConfigurationProperties(prefix = "crane")
@EnableConfigurationProperties
@Data
public class CraneAutoConfigurationProperties {

    private EnumConfigProperties enums = new EnumConfigProperties();

    private CacheConfigProperties cache = new CacheConfigProperties();

    /**
     * 是否允许使用reflectasm优化反射性能
     */
    private boolean enableAsmReflect = true;

    /**
     * 枚举配置
     */
    @Data
    public static class EnumConfigProperties {

        /**
         * 需要预先注册到枚举指点中的枚举类
         */
        private Set<String> dictEnumPackages = new HashSet<>();

    }

    /**
     * 缓存配置
     */
    @Data
    public static class CacheConfigProperties {

        /**
         * 是否允许预解析类操作配置
         */
        private boolean enablePreParseClass = false;

        /**
         * 需要预解析并缓存配置的类所在的包路径
         */
        private Set<String> preParsedClassPackages = new HashSet<>();

        /**
         * 解析器bean名称，与需要使用对应解析器预解析并缓存配置的类所在的包路径
         */
        private Map<String, Set<String>> parserAndPreParsedClassPackages = new HashMap<>(4);

    }

}
