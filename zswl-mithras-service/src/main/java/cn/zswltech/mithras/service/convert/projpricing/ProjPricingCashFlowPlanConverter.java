package cn.zswltech.mithras.service.convert.projpricing;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.NumberUtil;
import cn.zswltech.mithras.dto.projpricing.cashflowplan.ProjPricingCashFlowPlanListRSP;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.excel.model.CashFlowExcelModel;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingCashFlowPlan;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingLeasePrice;
import cn.zswltech.mithras.projectprocess.service.bo.CashFlowBO;
import cn.zswltech.mithras.projectprocess.service.bo.CashFlowCalculateBO;

import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/9/6
 * @description
 */
public class ProjPricingCashFlowPlanConverter {
    public static CashFlowBO toCashFlowBO(ProjPricingCashFlowPlan projPricingCashFlowPlan) {
        CashFlowBO cashFlowBO = new CashFlowBO();
        cashFlowBO.setCashFlowDate(projPricingCashFlowPlan.getCashFlowDate());
        cashFlowBO.setCashFlowPhase(projPricingCashFlowPlan.getCashFlowPhase());
        cashFlowBO.setCashFlowAmount(projPricingCashFlowPlan.getCashFlowAmount());
        cashFlowBO.setRent(projPricingCashFlowPlan.getRent());
        cashFlowBO.setInterest(projPricingCashFlowPlan.getInterest());
        cashFlowBO.setPrincipal(projPricingCashFlowPlan.getPrincipal());
        cashFlowBO.setRemainingPrincipal(projPricingCashFlowPlan.getRemainingPrincipal());
        return cashFlowBO;
    }

    public static ProjPricingCashFlowPlanListRSP toCashFlowRSP(ProjPricingCashFlowPlan projPricingCashFlowPlan){
        ProjPricingCashFlowPlanListRSP rsp = new ProjPricingCashFlowPlanListRSP();
        rsp.setId(projPricingCashFlowPlan.getId());
        rsp.setDate(LocalDateTimeUtil.format(projPricingCashFlowPlan.getCashFlowDate(), DatePattern.NORM_DATE_PATTERN));
        rsp.setPhase(projPricingCashFlowPlan.getCashFlowPhase());
        rsp.setCashFlowAmount(projPricingCashFlowPlan.getCashFlowAmount());
        rsp.setRent(projPricingCashFlowPlan.getRent());
        rsp.setPrincipal(projPricingCashFlowPlan.getPrincipal());
        rsp.setInterest(projPricingCashFlowPlan.getInterest());
        rsp.setRemainingPrincipal(projPricingCashFlowPlan.getRemainingPrincipal());
        return rsp;
    }

    public static ProjPricingCashFlowPlan toProjPricingCashFlowPlan(Long projPricingId, CashFlowBO cashFlowBO) {
        ProjPricingCashFlowPlan cashFlowPlan = new ProjPricingCashFlowPlan();
        cashFlowPlan.setProjectId(projPricingId);
        cashFlowPlan.setCashFlowDate(cashFlowBO.getCashFlowDate());
        cashFlowPlan.setCashFlowPhase(cashFlowBO.getCashFlowPhase());
        cashFlowPlan.setCashFlowAmount(cashFlowBO.getCashFlowAmount());
        cashFlowPlan.setRent(cashFlowBO.getRent());
        cashFlowPlan.setInterest(cashFlowBO.getInterest());
        cashFlowPlan.setPrincipal(cashFlowBO.getPrincipal());
        cashFlowPlan.setRemainingPrincipal(cashFlowBO.getRemainingPrincipal());
        return cashFlowPlan;
    }

    public static CashFlowCalculateBO toCashFlowCalculateBO(ProjPricingLeasePrice leasePrice) {
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

    public static CashFlowExcelModel toCashFlowExcelModel(ProjPricingCashFlowPlan projReviewCashFlowPlan) {
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
