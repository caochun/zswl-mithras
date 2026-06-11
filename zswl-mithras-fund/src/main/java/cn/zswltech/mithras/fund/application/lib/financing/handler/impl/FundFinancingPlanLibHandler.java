package cn.zswltech.mithras.fund.application.lib.financing.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.fund.financing.plan.FundFinancingPlanDetailRSP;
import cn.zswltech.mithras.fund.application.lib.financing.FundFinancingDetailConverter;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingLibModelEnum;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingPlan;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingPlanLib;
import cn.zswltech.mithras.fund.application.lib.financing.handler.FundFinancingAbstractLibHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2023/2/21
 * @description
 */
@Component
public class FundFinancingPlanLibHandler extends FundFinancingAbstractLibHandler<FundFinancingPlanLib, FundFinancingPlan, FundFinancingPlanDetailRSP> {
    @Resource
    private FundFinancingDetailConverter fundFinancingDetailConverter;

    @Override
    protected FundFinancingPlanLib entity2Lib(FundFinancingPlan f) {
        return BeanUtil.copyProperties(f, FundFinancingPlanLib.class);
    }

    @Override
    protected FundFinancingPlan lib2Entity(FundFinancingPlanLib t) {
        return BeanUtil.copyProperties(t, FundFinancingPlan.class);
    }

    @Override
    protected FundFinancingPlanDetailRSP lib2Rsp(FundFinancingPlanLib f) {
        return fundFinancingDetailConverter.convertPlanToDetailRSP(this.actualLib2Entity(f));
    }

    @Override
    public FundFinancingLibModelEnum getSubModule() {
        return FundFinancingLibModelEnum.PLAN;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }

    @Override
    protected String businessModuleName() {
        return "FUND_FINANCING";
    }
}
