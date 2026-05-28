package cn.zswltech.mithras.service.service.lib.projreview.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanListRSP;
import cn.zswltech.mithras.service.enums.projreview.ProjReviewInfoModule;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewCashFlowQuotationProposal;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewCashFlowQuotationProposalLib;
import cn.zswltech.mithras.service.service.lib.projreview.handler.ProjReviewLibAbstractHandler;
import org.springframework.stereotype.Service;

/**
 * @author
 * @description
 * @since
 */
@Service
public class ProjReviewCashFlowQuotationProposalLibHandler
        extends ProjReviewLibAbstractHandler<ProjReviewCashFlowQuotationProposalLib, ProjReviewCashFlowQuotationProposal, ProjReviewCashFlowPlanListRSP> {

    @Override
    protected ProjReviewCashFlowQuotationProposalLib entity2Lib(ProjReviewCashFlowQuotationProposal f) {
        return BeanUtil.copyProperties(f, ProjReviewCashFlowQuotationProposalLib.class);
    }

    @Override
    protected ProjReviewCashFlowQuotationProposal lib2Entity(ProjReviewCashFlowQuotationProposalLib t) {
        return BeanUtil.copyProperties(t, ProjReviewCashFlowQuotationProposal.class);
    }

    @Override
    protected ProjReviewCashFlowPlanListRSP lib2Rsp(ProjReviewCashFlowQuotationProposalLib f) {
        ProjReviewCashFlowPlanListRSP rsp = BeanUtil.copyProperties(f, ProjReviewCashFlowPlanListRSP.class);
        rsp.setDate(LocalDateTimeUtil.format(f.getCashFlowDate(), DatePattern.NORM_DATE_PATTERN));
        rsp.setPhase(f.getCashFlowPhase());
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public ProjReviewInfoModule getSubModule() {
        return ProjReviewInfoModule.CASH_FACTORING_PICE;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
    
}
