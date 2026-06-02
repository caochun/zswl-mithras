package cn.zswltech.mithras.metric.emit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author yibin
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mithras.remote")
public class EmitRemoteCfg {

    private String pubKey;

    private String authOrg;

    private String hostAddr;
}
