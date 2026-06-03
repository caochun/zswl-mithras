package cn.zswltech.mithras.guanbao.config;

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
@ConfigurationProperties(prefix = "guanyuan")
public class GuanYuanConfigProperties {
    private String privateKey;
    private String publicKey;
    private String host;
    private String token;
    private String loginId;
    private String password;
    private String domain;
}
