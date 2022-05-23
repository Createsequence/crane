package top.xiajibagao.crane.starter;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
@Accessors(fluent = true)
@Data
public class CraneAutoConfigurationProperties {

    private Set<String> dictEnumPackages = new HashSet<>();
    private Map<String, Set<String>> preparseClassPackages = new HashMap<>(4);

}
