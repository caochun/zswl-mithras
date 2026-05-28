package cn.zswltech.mithras.service.service.third.overduereport.rsp;

import lombok.Data;

import java.util.List;

/**
 * @ClassName OverdueReportBatRSP
 * @Description TODO
 * @Author jackerhe
 * @Date 2025/9/10 11:14
 * @Version 1.0
 **/
@Data
public class OverdueReportBatSaveRSP extends OverdueReportBaseRSP{

    private List<OverdueReportBatSaveRSP.OverdueReportBatBody> data;

    @Data
    public static class OverdueReportBatBody {
        private String billno;
        private String msg;
        private String sourcebillno;
        private Boolean success;
    }
}
