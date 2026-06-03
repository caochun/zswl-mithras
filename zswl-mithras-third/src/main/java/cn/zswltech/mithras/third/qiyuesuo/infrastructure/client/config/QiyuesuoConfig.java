package cn.zswltech.mithras.third.qiyuesuo.infrastructure.client.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author bigbear
 * @date 2024/12/7 14:42
 * @description
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "qiyuesuo")
public class QiyuesuoConfig {
    private String server;
    private String clientId;
    private String clientSecret;
    private Long categoryId;
    private Headers headers;
    private String tenantName;
    private Long companySealId;
    private Long companyContractSealId;
    @Autowired
    private SealKeyword sealKeyword;

    @Data
    @Configuration
    @ConfigurationProperties(prefix = "qiyuesuo.headers")
    public static class Headers {
        private String accessToken;
        private String signature;
        private Long timestamp;
    }

    @Data
    @Configuration
    @ConfigurationProperties(prefix = "qiyuesuo.seal.keyword")
    public static class SealKeyword {
        private String contractSeal;
        private String contractSettleSeal;
    }
}
