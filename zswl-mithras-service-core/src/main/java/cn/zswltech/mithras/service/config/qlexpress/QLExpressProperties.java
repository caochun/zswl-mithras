package cn.zswltech.mithras.service.config.qlexpress;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author dingqi
 * @date 2023/2/16
 * @description
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "qlexpress")
public class QLExpressProperties {
    private boolean trace;
}
