package cn.zswltech.mithras.projectprocess.convert.projreview;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.NumberUtil;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanListRSP;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.projectprocess.excel.model.CashFlowExcelModel;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewCashFlowPlan;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.projectprocess.application.model.CashFlowBO;
import cn.zswltech.mithras.projectprocess.application.model.CashFlowCalculateBO;

import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/9/6
 * @description
 */
public class ProjReviewCashFlowPlanConverter {
    public static CashFlowBO toCashFlowBO(ProjReviewCashFlowPlan projReviewCashFlowPlan) {
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

    public static ProjReviewCashFlowPlanListRSP toCashFlowRSP(ProjReviewCashFlowPlan projReviewCashFlowPlan){
        ProjReviewCashFlowPlanListRSP rsp = new ProjReviewCashFlowPlanListRSP();
        rsp.setId(projReviewCashFlowPlan.getId());
        rsp.setDate(LocalDateTimeUtil.format(projReviewCashFlowPlan.getCashFlowDate(), DatePattern.NORM_DATE_PATTERN));
        rsp.setPhase(projReviewCashFlowPlan.getCashFlowPhase());
        rsp.setCashFlowAmount(projReviewCashFlowPlan.getCashFlowAmount());
        rsp.setRent(projReviewCashFlowPlan.getRent());
        rsp.setPrincipal(projReviewCashFlowPlan.getPrincipal());
        rsp.setInterest(projReviewCashFlowPlan.getInterest());
        rsp.setRemainingPrincipal(projReviewCashFlowPlan.getRemainingPrincipal());
        return rsp;
    }

    public static ProjReviewCashFlowPlan toProjReviewCashFlowPlan(Long projReviewId, CashFlowBO cashFlowBO) {
        ProjReviewCashFlowPlan cashFlowPlan = new ProjReviewCashFlowPlan();
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

    public static CashFlowCalculateBO toCashFlowCalculateBO(ProjReviewLeasePrice leasePrice) {
        CashFlowCalculateBO cashFlowCalculateBO = new CashFlowCalculateBO();
        cashFlowCalculateBO.setCreditAmount(leasePrice.getApplyCreditAmount() == null ? 0L : leasePrice.getApplyCreditAmount());
        cashFlowCalculateBO.setConsultingFee(leasePrice.getConsultingFee() == null ? 0L : leasePrice.getConsultingFee());
        cashFlowCalculateBO.setEarnestMoney(leasePrice.getEarnestMoney() == null ? 0L : leasePrice.getEarnestMoney());
        cashFlowCalculateBO.setDownPayment(leasePrice.getDownPayment() == null ? 0L : leasePrice.getDownPayment());
        cashFlowCalculateBO.setNominalPrice(leasePrice.getNominalPrice() == null ? 0L : leasePrice.getNominalPrice());
        cashFlowCalculateBO.setStartDate(leasePrice.getPlannedStartingDate());
        cashFlowCalculateBO.setRepayTimes(leasePrice.getRepayTimesTotal());
        cashFlowCalculateBO.setRepayRate(leasePrice.getRepayRate());
        cashFlowCalculateBO.setInterestRate(leasePrice.getLeaseRatePercent());
        cashFlowCalculateBO.setPayType(leasePrice.getPayType());
        cashFlowCalculateBO.setRentalCalcType(leasePrice.getRentalCalcType());
        cashFlowCalculateBO.setInterestWay(leasePrice.getInterestWay());
        cashFlowCalculateBO.setTotalMonth(leasePrice.getLeaseMonthCount());
        return cashFlowCalculateBO;
    }

    public static CashFlowExcelModel toCashFlowExcelModel(ProjReviewCashFlowPlan projReviewCashFlowPlan) {
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
}
