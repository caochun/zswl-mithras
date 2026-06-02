package cn.zswltech.mithras.third.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2023/8/7
 * @description
 */
@Data
@Component
@ConfigurationProperties(prefix = "yunhu")
public class YunHuConfig {
    private String baseUrl;
    private String accessKeyId;
    private String accessKeySecret;
}
