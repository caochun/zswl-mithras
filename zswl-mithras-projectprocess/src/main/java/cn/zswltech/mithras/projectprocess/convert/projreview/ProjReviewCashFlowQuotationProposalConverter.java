package cn.zswltech.mithras.projectprocess.convert.projreview;

import cn.hutool.core.util.NumberUtil;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.projectprocess.excel.model.CashFlowExcelModel;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewCashFlowPlan;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewCashFlowQuotationProposal;
import cn.zswltech.mithras.projectprocess.service.bo.CashFlowBO;

import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/9/6
 * @description
 */
public class ProjReviewCashFlowQuotationProposalConverter {
    public static CashFlowBO toCashFlowBO(ProjReviewCashFlowQuotationProposal projReviewCashFlowPlan) {
        CashFlowBO cashFlowBO = new CashFlowBO();
        cashFlowBO.setCashFlowDate(projReviewCashFlowPlan.getCashFlowDate());
        cashFlowBO.setCashFlowPhase(projReviewCashFlowPlan.getCashFlowPhase());
        cashFlowBO.setCashFlowAmount(projReviewCashFlowPlan.getCashFlowAmount());
        cashFlowBO.setRent(projReviewCashFlowPlan.getRent());
        cashFlowBO.setInterest(projReviewCashFlowPlan.getInterest());
        cashFlowBO.setPrincipal(projReviewCashFlowPlan.getPrincipal());
        cashFlowBO.setRemainingPrincipal(projReviewCashFlowPlan.getRemainingPrincipal());
        return cashFlowBO;
    }

    public static ProjReviewCashFlowQuotationProposal toProjReviewCashFlowPlan(Long projReviewId, CashFlowBO cashFlowBO) {
        ProjReviewCashFlowQuotationProposal cashFlowPlan = new ProjReviewCashFlowQuotationProposal();
        cashFlowPlan.setProjectId(projReviewId);
        cashFlowPlan.setCashFlowDate(cashFlowBO.getCashFlowDate());
        cashFlowPlan.setCashFlowPhase(cashFlowBO.getCashFlowPhase());
        cashFlowPlan.setCashFlowAmount(cashFlowBO.getCashFlowAmount());
        cashFlowPlan.setRent(cashFlowBO.getRent());
        cashFlowPlan.setInterest(cashFlowBO.getInterest());
        cashFlowPlan.setPrincipal(cashFlowBO.getPrincipal());
        cashFlowPlan.setRemainingPrincipal(cashFlowBO.getRemainingPrincipal());
        return cashFlowPlan;
    }

    public static CashFlowExcelModel toCashFlowExcelModel(ProjReviewCashFlowQuotationProposal projReviewCashFlowPlan) {
        CashFlowExcelModel cashFlowExcelModel = new CashFlowExcelModel();
        cashFlowExcelModel.setCashFlowDate(projReviewCashFlowPlan.getCashFlowDate());
        cashFlowExcelModel.setCashFlowPhase(projReviewCashFlowPlan.getCashFlowPhase());
        if (Objects.isNull(projReviewCashFlowPlan.getRent())) {
            projReviewCashFlowPlan.setRent(0L);
        }
        cashFlowExcelModel.setRent(NumberUtil.div(projReviewCashFlowPlan.getRent().toString(), GlobalConstants.MONEY_MULTIPLE));
        if (Objects.isNull(projReviewCashFlowPlan.getCashFlowAmount())) {
            projReviewCashFlowPlan.setCashFlowAmount(0L);
        }
        cashFlowExcelModel.setCashFlowAmount(NumberUtil.div(projReviewCashFlowPlan.getCashFlowAmount().toString(), GlobalConstants.MONEY_MULTIPLE));
        if (Objects.isNull(projReviewCashFlowPlan.getPrincipal())) {
            projReviewCashFlowPlan.setPrincipal(0L);
        }
        cashFlowExcelModel.setPrincipal(NumberUtil.div(projReviewCashFlowPlan.getPrincipal().toString(), GlobalConstants.MONEY_MULTIPLE));
        if (Objects.isNull(projReviewCashFlowPlan.getInterest())) {
            projReviewCashFlowPlan.setInterest(0L);
        }
        cashFlowExcelModel.setInterest(NumberUtil.div(projReviewCashFlowPlan.getInterest().toString(), GlobalConstants.MONEY_MULTIPLE));
        if (Objects.isNull(projReviewCashFlowPlan.getRemainingPrincipal())) {
            projReviewCashFlowPlan.setRemainingPrincipal(0L);
        }
        cashFlowExcelModel.setRemainingPrincipal(NumberUtil.div(projReviewCashFlowPlan.getRemainingPrincipal().toString(), GlobalConstants.MONEY_MULTIPLE));
        return cashFlowExcelModel;
    }

    public static ProjReviewCashFlowPlan toProjReviewCashFlowPlan(ProjReviewCashFlowQuotationProposal projReviewCashFlowQuotationProposal) {
        ProjReviewCashFlowPlan cashFlowPlan = new ProjReviewCashFlowPlan();
        cashFlowPlan.setId(projReviewCashFlowQuotationProposal.getId());
        cashFlowPlan.setProjectId(projReviewCashFlowQuotationProposal.getProjectId());
        cashFlowPlan.setCashFlowDate(projReviewCashFlowQuotationProposal.getCashFlowDate());
        cashFlowPlan.setCashFlowPhase(projReviewCashFlowQuotationProposal.getCashFlowPhase());
        cashFlowPlan.setCashFlowAmount(projReviewCashFlowQuotationProposal.getCashFlowAmount());
        cashFlowPlan.setRent(projReviewCashFlowQuotationProposal.getRent());
        cashFlowPlan.setPrincipal(projReviewCashFlowQuotationProposal.getPrincipal());
        cashFlowPlan.setInterest(projReviewCashFlowQuotationProposal.getInterest());
        cashFlowPlan.setRemainingPrincipal(projReviewCashFlowQuotationProposal.getRemainingPrincipal());
        cashFlowPlan.setCreateBy(projReviewCashFlowQuotationProposal.getCreateBy());
        cashFlowPlan.setCreateTime(projReviewCashFlowQuotationProposal.getCreateTime());
        cashFlowPlan.setUpdateBy(projReviewCashFlowQuotationProposal.getUpdateBy());
        cashFlowPlan.setUpdateTime(projReviewCashFlowQuotationProposal.getUpdateTime());
        return cashFlowPlan;
    }
}
