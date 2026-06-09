package cn.zswltech.mithras.system.identity.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author dingqi
 * @date 2023/2/28
 * @description
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "apphost")
public class AppConfigProperties {
    private String password;
}
