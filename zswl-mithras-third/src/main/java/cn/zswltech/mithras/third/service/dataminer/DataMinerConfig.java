package cn.zswltech.mithras.third.service.dataminer;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author dingqi
 * @date 2025/3/17
 * @description
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "dataminer")
public class DataMinerConfig {
    private String protocol;
    private String host;
    private String authSubject;
    private String authSecret;
}
