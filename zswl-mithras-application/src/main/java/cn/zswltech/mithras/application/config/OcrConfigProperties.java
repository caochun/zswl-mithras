package cn.zswltech.mithras.application.config;

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
@ConfigurationProperties(prefix = "ocr")
public class OcrConfigProperties {
    private String host;
    private Integer probLimit;
    private Boolean mock;
    private String appId;
    private String secretCode;
}
