package cn.zswltech.mithras.third.financialshare.infrastructure.client.req;

import lombok.Data;

/**
 * @ClassName AccessTokenREQ
 * @Description
 * @Author jackerhe
 * @Date 2022/10/28 11:15 上午
 * @Version 1.0
 **/
@Data
public class AccessTokenREQ {

    private String user;

    private String apptoken;

    private String tenantid;

    private String accountId;

    private String usertype;
}
