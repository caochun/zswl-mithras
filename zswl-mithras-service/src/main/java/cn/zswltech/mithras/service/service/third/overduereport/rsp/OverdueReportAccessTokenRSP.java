package cn.zswltech.mithras.service.service.third.overduereport.rsp;

import lombok.Data;

@Data
public class OverdueReportAccessTokenRSP extends OverdueReportBaseRSP {

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
