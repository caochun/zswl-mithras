package cn.zswltech.mithras.contract.convert.contract;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.dto.contract.settle.ContractSettlePlanDetailRSP;
import cn.zswltech.mithras.dto.contract.settle.ContractSettlePlanNormalREQ;
import cn.zswltech.mithras.contract.model.contract.ContractSettlePlan;

import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/12/15
 * @description
 */
public class ContractSettlePlanConvert {
    public static ContractSettlePlanDetailRSP toContractSettlePlanDetailRSP(ContractSettlePlan contractSettlePlan) {
        ContractSettlePlanDetailRSP rsp = new ContractSettlePlanDetailRSP();
        rsp.setId(contractSettlePlan.getId());
        rsp.setContractId(contractSettlePlan.getContractId());
        rsp.setOutstandingRent(contractSettlePlan.getOutstandingRent());
        rsp.setBeforeMaturityPrincipal(contractSettlePlan.getBeforeMaturityPrincipal());
        rsp.setBeforeMaturityInterest(contractSettlePlan.getBeforeMaturityInterest());
        rsp.setLoss(contractSettlePlan.getLoss());
        rsp.setLiquidatedDamages(contractSettlePlan.getLiquidatedDamages());
        rsp.setEarnestBalance(contractSettlePlan.getEarnestBalance());
        rsp.setIsEarnestDeduction(contractSettlePlan.getIsEarnestDeduction());
        rsp.setNominalPrice(contractSettlePlan.getNominalPrice());
        rsp.setApplyDerateAmount(contractSettlePlan.getApplyDerateAmount());
        if (Objects.nonNull(contractSettlePlan.getOriginalDeadline())) {
            rsp.setOriginalDeadline(LocalDateTimeUtil.format(contractSettlePlan.getOriginalDeadline(), DatePattern.NORM_DATE_PATTERN));
        }
        if (Objects.nonNull(contractSettlePlan.getApplySettleDate())) {
            rsp.setApplySettleDate(LocalDateTimeUtil.format(contractSettlePlan.getApplySettleDate(), DatePattern.NORM_DATE_PATTERN));
        }
        rsp.setSettleRemark(contractSettlePlan.getSettleRemark());
        return rsp;
    }

    public static ContractSettlePlan toContractSettlePlan(ContractSettlePlanNormalREQ contractSettlePlanNormalREQ) {
        ContractSettlePlan contractSettlePlan = new ContractSettlePlan();
        contractSettlePlan.setContractId(contractSettlePlanNormalREQ.getContractId());
        contractSettlePlan.setOutstandingRent(contractSettlePlanNormalREQ.getOutstandingRent());
        contractSettlePlan.setBeforeMaturityPrincipal(contractSettlePlanNormalREQ.getBeforeMaturityPrincipal());
        contractSettlePlan.setLoss(contractSettlePlanNormalREQ.getLoss());
        contractSettlePlan.setLiquidatedDamages(contractSettlePlanNormalREQ.getLiquidatedDamages());
        contractSettlePlan.setEarnestBalance(contractSettlePlanNormalREQ.getEarnestBalance());
        contractSettlePlan.setIsEarnestDeduction(contractSettlePlanNormalREQ.getIsEarnestDeduction());
        contractSettlePlan.setNominalPrice(contractSettlePlanNormalREQ.getNominalPrice());
        contractSettlePlan.setApplyDerateAmount(contractSettlePlanNormalREQ.getApplyDerateAmount());
        contractSettlePlan.setOriginalDeadline(LocalDateTimeUtil.parse(contractSettlePlanNormalREQ.getOriginalDeadline(), DatePattern.NORM_DATE_PATTERN).toLocalDate());
        contractSettlePlan.setBeforeMaturityInterest(contractSettlePlanNormalREQ.getBeforeMaturityInterest());
        return contractSettlePlan;
    }
}
