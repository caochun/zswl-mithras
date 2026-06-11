package cn.zswltech.mithras.projectprocess.application.lib.projpricing.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.dto.projpricing.cashflowplan.ProjPricingCashFlowPlanListRSP;
import cn.zswltech.mithras.projectprocess.enums.projpricing.ProjPricingInfoModule;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingCashFlowPlan;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingCashFlowPlanLib;
import cn.zswltech.mithras.projectprocess.application.lib.projpricing.handler.ProjPricingLibAbstractHandler;
import org.springframework.stereotype.Service;

/**
 * @author
 * @description
 * @since
 */
@Service
public class ProjPricingCashFlowPlanLibHandler
        extends ProjPricingLibAbstractHandler<ProjPricingCashFlowPlanLib, ProjPricingCashFlowPlan, ProjPricingCashFlowPlanListRSP> {

    @Override
    protected ProjPricingCashFlowPlanLib entity2Lib(ProjPricingCashFlowPlan f) {
        return BeanUtil.copyProperties(f, ProjPricingCashFlowPlanLib.class);
    }

    @Override
    protected ProjPricingCashFlowPlan lib2Entity(ProjPricingCashFlowPlanLib t) {
        return BeanUtil.copyProperties(t, ProjPricingCashFlowPlan.class);
    }

    @Override
    protected ProjPricingCashFlowPlanListRSP lib2Rsp(ProjPricingCashFlowPlanLib f) {
        ProjPricingCashFlowPlanListRSP rsp = BeanUtil.copyProperties(f, ProjPricingCashFlowPlanListRSP.class);
        rsp.setDate(LocalDateTimeUtil.format(f.getCashFlowDate(), DatePattern.NORM_DATE_PATTERN));
        rsp.setPhase(f.getCashFlowPhase());
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public ProjPricingInfoModule getSubModule() {
        return ProjPricingInfoModule.CASH_FACTORING_PICE;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
    
}
