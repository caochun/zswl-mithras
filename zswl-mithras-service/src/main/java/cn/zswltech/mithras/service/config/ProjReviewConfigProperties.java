package cn.zswltech.mithras.service.config;

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
@ConfigurationProperties(prefix = "projreview")
public class ProjReviewConfigProperties {

    private Boolean onSiteDueDiligence;

}
