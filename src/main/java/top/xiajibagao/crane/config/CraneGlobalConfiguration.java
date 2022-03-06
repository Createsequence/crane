package top.xiajibagao.crane.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 全局配置
 *
 * @author huangchengxing
 * @date 2022/03/03 13:57
 */
@Getter
@Configuration
@ConfigurationProperties(prefix = "crane")
public class CraneGlobalConfiguration {

    /**
     * 是否忽略处理过程发生异常
     */
    private boolean ignoreException;

}
