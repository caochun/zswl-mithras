package cn.zswltech.mithras.third.overduereport.client.rsp;

import lombok.Data;


@Data
public class OverdueReportAppTokenRSP extends OverdueReportBaseRSP {

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
