package cn.zswltech.mithras.third.financialshare.infrastructure.client.resp;

import lombok.Data;

/**
 * @ClassName AppTokenRSP
 * @Description
 * @Author jackerhe
 * @Date 2022/10/28 11:34 上午
 * @Version 1.0
 **/
@Data
public class AppTokenRSP extends FinancialBaseRSP{

    private AppTokenData data;

    @Data
    public class AppTokenData{
        private String app_token;
        private String success;
        private String error_desc;
        private String expire_time;
        private String error_code;
    }

}
