package cn.zswltech.mithras.third.service.financial.resp;

import lombok.Data;

/**
 * 苍穹2期 通用应收单_返回体
 **/
@Data
public class CQ2FinancialBaseRSP extends FinancialBaseRSP {

    private CQ2PlanCollectionRspBody data;

    @Data
    public class CQ2PlanCollectionRspBody{
        private Integer failCount;
        private String result;
        private Integer successCount;
    }

}