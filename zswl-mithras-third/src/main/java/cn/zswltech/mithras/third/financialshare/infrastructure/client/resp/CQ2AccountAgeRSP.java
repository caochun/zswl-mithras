package cn.zswltech.mithras.third.financialshare.infrastructure.client.resp;

import lombok.Data;

import java.util.List;

@Data
public class CQ2AccountAgeRSP extends FinancialBaseRSP {

    private CQ2AccountAgeRSP.CQ2AccountAgeAddRspBody data;

    @Data
    public static class CQ2AccountAgeAddRspBody {
        private Integer failCount;
        private List<CQ2AccountAgeRSP.CQ2AccountAgeAddRspResult> result;
        private Integer successCount;
    }

    @Data
    public static class CQ2AccountAgeAddRspResult {
        private Integer billIndex;
        private boolean billStatus;
        private Long id;
        private List<String> keys;
        private String number;
        private String type;
    }

}