package cn.zswltech.mithras.service.service.lib.projreview.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanListRSP;
import cn.zswltech.mithras.service.enums.projreview.ProjReviewInfoModule;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewCashFlowPlan;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewCashFlowPlanLib;
import cn.zswltech.mithras.service.service.lib.projreview.handler.ProjReviewLibAbstractHandler;
import org.springframework.stereotype.Service;

/**
 * @author
 * @description
 * @since
 */
@Service
public class ProjReviewCashFlowPlanLibHandler
        extends ProjReviewLibAbstractHandler<ProjReviewCashFlowPlanLib, ProjReviewCashFlowPlan, ProjReviewCashFlowPlanListRSP> {

    @Override
    protected ProjReviewCashFlowPlanLib entity2Lib(ProjReviewCashFlowPlan f) {
        return BeanUtil.copyProperties(f, ProjReviewCashFlowPlanLib.class);
    }

    @Override
    protected ProjReviewCashFlowPlan lib2Entity(ProjReviewCashFlowPlanLib t) {
        return BeanUtil.copyProperties(t, ProjReviewCashFlowPlan.class);
    }

    @Override
    protected ProjReviewCashFlowPlanListRSP lib2Rsp(ProjReviewCashFlowPlanLib f) {
        ProjReviewCashFlowPlanListRSP rsp = BeanUtil.copyProperties(f, ProjReviewCashFlowPlanListRSP.class);
        rsp.setDate(LocalDateTimeUtil.format(f.getCashFlowDate(), DatePattern.NORM_DATE_PATTERN));
        rsp.setPhase(f.getCashFlowPhase());
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public ProjReviewInfoModule getSubModule() {
        return ProjReviewInfoModule.MEETING_MINUTES_CASH_FACTORING_PICE;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
    
}
