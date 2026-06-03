package cn.zswltech.mithras.third.financialshare.infrastructure.client.config;

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
@ConfigurationProperties(prefix = "cq.auth")
public class AppAuthConfig {

    private String appId;

    private String appSecuret;

    private String tenantid;

    private String accountId;

    //private String language;

    private String user;

    private String user2;

    private String user3;

   /* private String apptoken;*/

    private String usertype;

    private String appBaseUrl;

    private String clientId;

    private String clientNo;

}
