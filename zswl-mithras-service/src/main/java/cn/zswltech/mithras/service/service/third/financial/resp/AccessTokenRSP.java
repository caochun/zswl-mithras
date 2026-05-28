package cn.zswltech.mithras.service.service.third.financial.resp;

import lombok.Data;

/**
 * @ClassName AppTokenRSP
 * @Description
 * @Author jackerhe
 * @Date 2022/10/28 11:34 上午
 * @Version 1.0
 **/
@Data
public class AccessTokenRSP extends FinancialBaseRSP{

    private AccessTokenData data;

    @Data
    public class AccessTokenData{
        private String access_token;
        private String success;
        private String error_desc;
        private String expire_time;
        private String error_code;
    }

}
