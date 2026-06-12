package cn.zswltech.mithras.fund.application.convert.financing;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.fund.financing.FundFinancingListRSP;
import cn.zswltech.mithras.fund.model.financing.FundFinancingCreditRef;
import cn.zswltech.mithras.fund.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.model.financing.FundFinancingPlan;
import cn.zswltech.mithras.fund.model.financing.FundFinancingPledgeInfo;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/2/20
 * @description
 */
public class FundFinancingConvert {
    public static FundFinancingListRSP.FundFinancingList toFundFinancingList(FundFinancingBaseInfo baseInfo, FundFinancingPlan plan, List<FundFinancingPledgeInfo> pledgeInfoList, List<FundFinancingCreditRef> refList) {
        FundFinancingListRSP.FundFinancingList rsp = new FundFinancingListRSP.FundFinancingList();
        rsp.setId(baseInfo.getId());
        rsp.setFinancingCode(baseInfo.getFinancingCode());
        if(CollectionUtil.isNotEmpty(refList)) {
            rsp.setOrganizationId(refList.stream().map(FundFinancingCreditRef::getOrganizationId).collect(Collectors.toList()));
        }
        rsp.setComprehensiveFinancingCost(plan.getComprehensiveInterestRate());
        rsp.setFinancingAmount(plan.getFinancingAmount());
        rsp.setBusinessType(baseInfo.getBusinessType());
        if (StrUtil.isNotBlank(baseInfo.getGuaranteeInfo())) {
            List<FundFinancingPlan.GuaranteeInfo> guaranteeInfoList = JSONUtil.toList(plan.getGuaranteeInfo(), FundFinancingPlan.GuaranteeInfo.class);
            long guaranteeAmount = 0;
            for (FundFinancingPlan.GuaranteeInfo guaranteeInfo : guaranteeInfoList) {
                guaranteeAmount = guaranteeAmount + guaranteeInfo.getGuaranteeAmount();
            }
            rsp.setGuaranteeFinancingAmount(guaranteeAmount);
        }
        if (Objects.nonNull(rsp.getGuaranteeFinancingAmount())) {
            rsp.setCreditFinancingAmount(plan.getFinancingAmount() - rsp.getGuaranteeFinancingAmount());
        } else {
            rsp.setCreditFinancingAmount(plan.getFinancingAmount());
        }
        rsp.setActualComprehensiveCost(plan.getComprehensiveInterestRate());
        rsp.setInterestRateType(plan.getInterestRateType());
        if (Objects.nonNull(plan.getLprRatePercent()) && Objects.nonNull(plan.getLprAddPercent())) {
            rsp.setInterestRate(plan.getLprRatePercent() + plan.getLprAddPercent());
        }
        if (Objects.nonNull(baseInfo.getActualLoanDate())) {
            rsp.setBorrowDate(LocalDateTimeUtil.format(baseInfo.getActualLoanDate(), DatePattern.NORM_DATE_PATTERN));
        }
        if (Objects.nonNull(baseInfo.getActualExpireDate())) {
            rsp.setExpireDate(LocalDateTimeUtil.format(baseInfo.getActualExpireDate(), DatePattern.NORM_DATE_PATTERN));
        }
        rsp.setFinancingStatus(baseInfo.getFinancingStatus());
        rsp.setApprovalStatus(baseInfo.getApprovalStatus());
        rsp.setChangeSubType(baseInfo.getChangeSubType());
        if (CollectionUtil.isNotEmpty(pledgeInfoList)) {
            rsp.setContractCodeList(pledgeInfoList.stream().map(FundFinancingPledgeInfo::getContractCode).collect(Collectors.toList()));
        }
        if (Objects.nonNull(baseInfo.getCreateTime())) {
            rsp.setCreateTime(LocalDateTimeUtil.format(baseInfo.getCreateTime(), DatePattern.NORM_DATETIME_PATTERN));
        }
        if (Objects.nonNull(baseInfo.getUpdateTime())) {
            rsp.setUpdateTime(LocalDateTimeUtil.format(baseInfo.getUpdateTime(), DatePattern.NORM_DATETIME_PATTERN));
        }
        return rsp;
    }
}
