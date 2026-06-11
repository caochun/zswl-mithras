package cn.zswltech.mithras.application.orchestration.adapter.fund;

import cn.zswltech.mithras.dto.fund.financing.baseinfo.FundFinancingBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.fund.financing.earlysettle.FundFinancingEarlySettlePlanRSP;
import cn.zswltech.mithras.dto.fund.financing.plan.FundFinancingPlanDetailRSP;
import cn.zswltech.mithras.dto.fund.financing.pledge.FundFinancingPledgeListRSP;
import cn.zswltech.mithras.fund.application.lib.financing.FundFinancingDetailConverter;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingEarlySettlePlan;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingPlan;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingEarlySettlePlanService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingPlanService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingPledgeInfoService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class FundFinancingDetailConverterAdapter implements FundFinancingDetailConverter {

    @Resource
    private FundFinancingBaseInfoService fundFinancingBaseInfoService;
    @Resource
    private FundFinancingEarlySettlePlanService fundFinancingEarlySettlePlanService;
    @Resource
    private FundFinancingPlanService fundFinancingPlanService;
    @Resource
    private FundFinancingPledgeInfoService fundFinancingPledgeInfoService;

    @Override
    public FundFinancingBaseInfoDetailRSP convertBaseInfoToDetailRSP(FundFinancingBaseInfo baseInfo, boolean hasModify) {
        return fundFinancingBaseInfoService.convertToDetailRSP(baseInfo, hasModify);
    }

    @Override
    public FundFinancingEarlySettlePlanRSP convertEarlySettlePlanToRSP(FundFinancingEarlySettlePlan earlySettlePlan, Long financingId) {
        return fundFinancingEarlySettlePlanService.convertToRSP(earlySettlePlan, financingId);
    }

    @Override
    public FundFinancingPlanDetailRSP convertPlanToDetailRSP(FundFinancingPlan plan) {
        return fundFinancingPlanService.convertToDetailRSP(plan);
    }

    @Override
    public List<FundFinancingPledgeListRSP> convertPledgeInfoToRSPList(List<FundFinancingPledgeInfo> pledgeInfoList) {
        return fundFinancingPledgeInfoService.convertToRSPList(pledgeInfoList);
    }
}
