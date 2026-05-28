package cn.zswltech.mithras.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/3 17:15
 */

@Data
@Component
@ConfigurationProperties(prefix = "mithras")
public class CrosOriginWhiteListProperties {

    private List<String> corsWhiteList;

}
