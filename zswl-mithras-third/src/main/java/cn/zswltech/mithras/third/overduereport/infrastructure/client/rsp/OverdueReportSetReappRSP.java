package cn.zswltech.mithras.third.overduereport.infrastructure.client.rsp;

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
public class OverdueReportSetReappRSP extends OverdueReportBaseRSP{

    private List<OverdueReportSetBatSaveRSP.OverdueReportSetBatBody> data;

    @Data
    public static class OverdueReportSetBatBody {
        private Integer failCount;
        private String filter;
        private String result;
        private Integer successCount;
        private Integer totalCount;
    }


}
