package cn.zswltech.mithras.creditreport.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName AppAuthVo
 * @Description
 * @Author jackerhe
 * @Date 2022/10/20 7:19 下午
 * @Version 1.0
 **/
@Data
@Component
@ConfigurationProperties(prefix = "xj.report")
public class CreditReportAppAuthConfig {

    private String baseUrl;

    private String password;

    private String account;

}
