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
public class OverdueReportSetBatSaveRSP extends OverdueReportBaseRSP{

    private List<OverdueReportSetBatSaveRSP.OverdueReportSetBatBody> data;

    @Data
    public static class OverdueReportSetBatBody {
        private String billno;
        private String msg;
        private String sourcebillno;
        private Boolean success;
    }
}
