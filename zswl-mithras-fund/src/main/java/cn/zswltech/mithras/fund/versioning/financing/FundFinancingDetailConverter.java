package cn.zswltech.mithras.fund.versioning.financing;

import cn.zswltech.mithras.dto.fund.financing.baseinfo.FundFinancingBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.fund.financing.earlysettle.FundFinancingEarlySettlePlanRSP;
import cn.zswltech.mithras.dto.fund.financing.plan.FundFinancingPlanDetailRSP;
import cn.zswltech.mithras.dto.fund.financing.pledge.FundFinancingPledgeListRSP;
import cn.zswltech.mithras.fund.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.model.financing.FundFinancingEarlySettlePlan;
import cn.zswltech.mithras.fund.model.financing.FundFinancingPlan;
import cn.zswltech.mithras.fund.model.financing.FundFinancingPledgeInfo;

import java.util.List;

public interface FundFinancingDetailConverter {

    default FundFinancingBaseInfoDetailRSP convertBaseInfoToDetailRSP(FundFinancingBaseInfo baseInfo, boolean hasModify) {
        throw new UnsupportedOperationException();
    }

    default FundFinancingEarlySettlePlanRSP convertEarlySettlePlanToRSP(FundFinancingEarlySettlePlan earlySettlePlan, Long financingId) {
        throw new UnsupportedOperationException();
    }

    default FundFinancingPlanDetailRSP convertPlanToDetailRSP(FundFinancingPlan plan) {
        throw new UnsupportedOperationException();
    }

    default List<FundFinancingPledgeListRSP> convertPledgeInfoToRSPList(List<FundFinancingPledgeInfo> pledgeInfoList) {
        throw new UnsupportedOperationException();
    }
}
