package top.xiajibagao.crane.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import top.xiajibagao.crane.core.helper.EnumDict;

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
@Data
public class CraneAutoConfigurationProperties {

    /**
     * 需要预先扫描注册到{@link EnumDict}的包路径
     */
    private Set<String> dictEnumPackages = new HashSet<>();

    /**
     * 是否允许预解析类操作配置
     */
    private boolean enablePreParse = false;

    /**
     * 需要预解析并缓存配置的类所在的包路径
     */
    private Set<String> preParsedClassPackages = new HashSet<>();

    /**
     * 解析器bean名称，与需要使用对应解析器预解析并缓存配置的类所在的包路径
     */
    private Map<String, Set<String>> parserAndPreParsedClassPackages = new HashMap<>(4);

}
