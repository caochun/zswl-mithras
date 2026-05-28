package cn.zswltech.mithras.service.service.third.financial.req;

import lombok.Data;

/**
 * @ClassName AppTokenREQ
 * @Description
 * @Author jackerhe
 * @Date 2022/10/28 11:14 上午
 * @Version 1.0
 **/
@Data
public class AppTokenREQ {

    private String appId;

    private String appSecuret;

    private String tenantid;

    private String accountId;

    private String language;
}
