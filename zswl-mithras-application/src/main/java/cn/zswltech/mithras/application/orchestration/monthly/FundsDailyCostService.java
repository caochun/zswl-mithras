package cn.zswltech.mithras.application.orchestration.monthly;

import cn.hutool.core.util.NumberUtil;
import cn.zswltech.flow.core.util.ApplicationContextUtil;
import cn.zswltech.mithras.dto.interestpay.InterestPayCalDetailModifyREQ;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.monthly.enums.MonthlyModuleTypeEnum;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingRepayActual;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingPledgeInfoService;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingRepayActualService;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.fund.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.model.financing.FundFinancingPlan;
import cn.zswltech.mithras.fund.model.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.fund.model.financing.FundFinancingRepayActual;
import cn.zswltech.mithras.monthly.mapper.model.FundsDailyCost;
import cn.zswltech.mithras.monthly.mapper.FundsDailyCostMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingPlanService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingPledgeInfoService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingRepayActualService;
import cn.zswltech.mithras.monthly.event.MonthlyManageUpdateEvent;
import cn.zswltech.mithras.application.orchestration.util.FinancialUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/5/16
 * @description
 */
@Slf4j
@Service
public class FundsDailyCostService extends ServiceImpl<FundsDailyCostMapper, FundsDailyCost> {

    public static final String BEGINNING_ITEM_TEXT = "期初余额";

    private static final String DIFF_ITEM_TEXT = "差额";

    @Resource
    private FundFinancingRepayActualService fundFinancingRepayActualService;

    @Resource
    private FundDirectFinancingRepayActualService fundDirectFinancingRepayActualService;

    @Resource
    private FundFinancingPlanService fundFinancingPlanService;

    @Resource
    private FundFinancingPledgeInfoService fundFinancingPledgeInfoService;

    @Resource
    private FundDirectFinancingPledgeInfoService fundDirectFinancingPledgeInfoService;

    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    public void modify(InterestPayCalDetailModifyREQ req) {
        FundsDailyCost fundsDailyCost = this.getById(req.getId());
        if (Objects.isNull(fundsDailyCost)) {
            throw new MithrasException("目标数据不存在");
        }
        // 计算新老钆差金额的差值
        long diff = req.getFinancingCostDiff() - fundsDailyCost.getFinancingCostDiff();
        // 重算部分字段
        fundsDailyCost.setFinancingCost(fundsDailyCost.getFinancingCost() + diff);
        fundsDailyCost.setTotalCapitalCost(fundsDailyCost.getTotalCapitalCost() + diff);
        fundsDailyCost.setFinancingCostAfterTax(FinancialUtil.calculateAmountWithoutTax(fundsDailyCost.getFinancingCost(), fundsDailyCost.getTaxRate()).longValue());
        fundsDailyCost.setTotalCapitalCostAfterTax(FinancialUtil.calculateAmountWithoutTax(fundsDailyCost.getTotalCapitalCostAfterTax(), fundsDailyCost.getTaxRate()).longValue());
        // 更新修改值
        fundsDailyCost.setEndOfPeriodInterestBalance(req.getEndOfPeriodInterestBalance());
        fundsDailyCost.setFinancingCostDiff(req.getFinancingCostDiff());
        this.updateById(fundsDailyCost);
    }

//    public void doFundsCalculate(FundFinancingBaseInfo fundFinancingBaseInfo, LocalDate interestDate) {
//        Long financingId = fundFinancingBaseInfo.getId();
//        //期初余额
//        FundsDailyCost beginningDailyCost = getBeginningDailyCost(financingId, "DK");
//        if (!interestDate.isAfter(beginningDailyCost.getInterestDate())
//                && !interestDate.isEqual(beginningDailyCost.getInterestDate().plusDays(1))) {
//            return;
//        }
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        // 当日的成本计提计息数据
//        FundsDailyCost existRecord = null;
//        existRecord = getConfirmedSpecificDailyCost(financingId, interestDate, "DK");
//        FundsDailyCost tempExistRecord = getSpecificDailyCost(financingId, interestDate, "DK");;
//        if (tempExistRecord != null) {
//            existRecord = tempExistRecord;
//        }
//        // 前一日的成本计提计息数据
//        FundsDailyCost previousRecord = getSpecificDailyCost(financingId, interestDate.minusDays(1), "DK");
//
//        String itemText = formatter.format(interestDate);
//        Long financingAmount = 0L;
//        Long principleAmount = 0L;
//        Long interestAmount = 0L;
//        int financingRate = 0;
//        int dailyRate = 0;
//        Long totalCapitalCost = 0L;
//        Long totalCapitalCostAfterTax = 0L;
//        Long financingCost = 0L;
//        String type = "DK";
//        double taxRate = 0.06;
//        String propertyType = null;
//
//        if (previousRecord == null && interestDate.isEqual(beginningDailyCost.getInterestDate().plusDays(1))) {
//            financingAmount = beginningDailyCost.getFinancingAmount();
//            totalCapitalCost = beginningDailyCost.getTotalCapitalCost();
//            totalCapitalCostAfterTax = beginningDailyCost.getTotalCapitalCostAfterTax();
//        } else if (previousRecord != null){
//            if (YesOrNoNumberEnum.YES.getCode().equals(previousRecord.getIsConfirmed())
//                && existRecord != null) {
//                if (existRecord.getItemText().equalsIgnoreCase(DIFF_ITEM_TEXT)) {
//                    financingAmount = existRecord.getFinancingAmount() + existRecord.getPrincipleAmount();
//                    totalCapitalCost = previousRecord.getTotalCapitalCost() + existRecord.getTotalCapitalCost();
//                    totalCapitalCostAfterTax = previousRecord.getTotalCapitalCostAfterTax() + existRecord.getTotalCapitalCostAfterTax();
//                } else {
//                    financingAmount = existRecord.getFinancingAmount() + existRecord.getPrincipleAmount();
//                    totalCapitalCost = existRecord.getTotalCapitalCost() - existRecord.getFinancingCost();
//                    totalCapitalCostAfterTax = existRecord.getTotalCapitalCostAfterTax() - existRecord.getFinancingCost();
//                }
//            } else {
//                financingAmount = previousRecord.getFinancingAmount();
//                totalCapitalCost = previousRecord.getTotalCapitalCost();
//                totalCapitalCostAfterTax = previousRecord.getTotalCapitalCostAfterTax();
//            }
//        }
//        FundFinancingRepayActual fundFinancingRepayActual = getRepayActual(financingId, interestDate);
//        if (fundFinancingRepayActual != null) {
//            principleAmount = fundFinancingRepayActual.getPrincipleAmount();
//            interestAmount = fundFinancingRepayActual.getInterestAmount();
//            if (Objects.nonNull(principleAmount)) {
//                financingAmount -= principleAmount;
//            }
//        }
//
//        //间融-质押资产为保理 没有销项抵减，所以税率=0
//        //间融-质押资产为直租：13%
//        //其他：6%
//        LambdaQueryWrapper<FundFinancingPledgeInfo> query = Wrappers.lambdaQuery();
//        query.eq(FundFinancingPledgeInfo::getFinancingId, financingId)
//                .eq(FundFinancingPledgeInfo::getIsPledge, 1);
//        List<FundFinancingPledgeInfo> fundFinancingPledgeInfoList = fundFinancingPledgeInfoService.list(query);
//        if (fundFinancingPledgeInfoList != null && !fundFinancingPledgeInfoList.isEmpty()) {
//            fundFinancingPledgeInfoList = fundFinancingPledgeInfoList.stream()
//                    .sorted(Comparator.comparing(FundFinancingPledgeInfo::getContractAmount).reversed()).collect(Collectors.toList());
//            if (!fundFinancingPledgeInfoList.isEmpty()) {
//                //
//                FundFinancingPledgeInfo fundFinancingPledgeInfo = fundFinancingPledgeInfoList.get(0);
//                ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(fundFinancingPledgeInfo.getContractId());
//                if (contractBaseInfo != null) {
//                    if (ProjectBizType.BL.name().equalsIgnoreCase(contractBaseInfo.getBizType())) {
//                        taxRate = 0.0;
//                        propertyType = ProjectBizType.BL.name();
//                    } else if (ProjectBizType.ZL.name().equalsIgnoreCase(contractBaseInfo.getBizType())
//                            && LeaseType.zhi_zu.name().equalsIgnoreCase(contractBaseInfo.getLeaseType())) {
//                        taxRate = 0.13;
//                        propertyType = LeaseType.zhi_zu.name();
//                    } else if (ProjectBizType.ZL.name().equalsIgnoreCase(contractBaseInfo.getBizType())
//                            && !LeaseType.zhi_zu.name().equalsIgnoreCase(contractBaseInfo.getLeaseType())){
//                        propertyType= contractBaseInfo.getLeaseType();
//                    } else {
//                        propertyType = contractBaseInfo.getBizType();
//                    }
//                }
//            }
//        }
//
//        FundFinancingPlan plan = fundFinancingPlanService.getOneByFinancingId(financingId);
//        if (plan != null
//                && Objects.nonNull(plan.getLprRatePercent()) && Objects.nonNull(plan.getLprAddPercent())) {
//            financingRate = plan.getLprRatePercent() + plan.getLprAddPercent();
//            dailyRate = calculateDailyRate(financingRate).intValue();
//            financingCost = this.calculateInterest(financingRate, financingAmount);
//            if (Objects.nonNull(interestAmount)) {
//                financingCost -= interestAmount;
//            }
//            totalCapitalCost += financingCost;
//            //1.不含税利息：租金表中的利息总额/ （1+税率）， 保留2位小数
//            totalCapitalCostAfterTax = NumberUtil.div(totalCapitalCost, new BigDecimal(String.valueOf(1 + taxRate)), 2, RoundingMode.HALF_UP).longValue();
//        }
//
//        LocalDate lastDayOfThisMonth = interestDate.with(TemporalAdjusters.lastDayOfMonth());
//        FundsDailyCost newRecord = new FundsDailyCost();
//        newRecord.setDailyRate(dailyRate);
//        newRecord.setFinancingCost(financingCost + interestAmount);
//        newRecord.setInterestAmount(interestAmount);
//        newRecord.setFinancingRate(financingRate);
//        newRecord.setFinancingAmount(financingAmount);
//        newRecord.setFinancingId(financingId);
//        newRecord.setPrincipleAmount(principleAmount);
//        newRecord.setInterestDate(interestDate);
//        newRecord.setType(type);
//        newRecord.setPropertyType(propertyType);
//        newRecord.setItemText(itemText);
//        newRecord.setTotalCapitalCost(totalCapitalCost);
//        newRecord.setTotalCapitalCostAfterTax(totalCapitalCostAfterTax);
//        if (Objects.nonNull(existRecord)
//                && YesOrNoNumberEnum.NO.getCode().equals(existRecord.getIsConfirmed())
//                && !DIFF_ITEM_TEXT.equalsIgnoreCase(existRecord.getItemText())) {
//            newRecord.setId(existRecord.getId());
//            this.updateById(newRecord);
//            MonthlyManageUpdateEvent.DataObject dataObject = getDataObject(newRecord.getMainId(), interestDate);
//            ApplicationContextUtil.getApplicationContext().publishEvent(dataObject);
//        } else if ((existRecord == null && previousRecord != null)
//                || !newRecord.getTotalCapitalCost().equals(existRecord.getTotalCapitalCost())){
//            this.save(newRecord);
//        }
//        if (Objects.nonNull(existRecord)
//                && YesOrNoNumberEnum.YES.getCode().equals(existRecord.getIsConfirmed())
//                && lastDayOfThisMonth.equals(existRecord.getInterestDate())
//                && !newRecord.getTotalCapitalCost().equals(existRecord.getTotalCapitalCost())) {
//            FundsDailyCost previousDiffRecord = getDiffDailyCost(financingId, type);
//            FundsDailyCost diffRecord = new FundsDailyCost();
//            diffRecord.setMainId(existRecord.getMainId());
//            diffRecord.setInterestDate(interestDate.plusDays(1));
//            diffRecord.setFinancingId(financingId);
//            diffRecord.setType(type);
//            diffRecord.setItemText(DIFF_ITEM_TEXT);
//            diffRecord.setFinancingAmount(newRecord.getFinancingAmount());
//            diffRecord.setFinancingCost(financingCost+interestAmount);
//            diffRecord.setTotalCapitalCost(newRecord.getTotalCapitalCost() - existRecord.getTotalCapitalCost());
//            diffRecord.setTotalCapitalCostAfterTax(newRecord.getTotalCapitalCostAfterTax() - existRecord.getTotalCapitalCostAfterTax());
//            if (Objects.nonNull(previousDiffRecord)) {
//                diffRecord.setId(previousDiffRecord.getId());
//                this.updateById(diffRecord);
//                MonthlyManageUpdateEvent.DataObject dataObject = getDataObject(diffRecord.getMainId(), interestDate);
//                ApplicationContextUtil.getApplicationContext().publishEvent(dataObject);
//            } else {
//                this.save(diffRecord);
//            }
//        } else if (Objects.nonNull(previousRecord)
//                && YesOrNoNumberEnum.YES.getCode().equals(previousRecord.getIsConfirmed())
//                && lastDayOfThisMonth.equals(previousRecord.getInterestDate())
//                && !newRecord.getTotalCapitalCost().equals(previousRecord.getTotalCapitalCost())) {
//            FundsDailyCost previousDiffRecord = getDiffDailyCost(financingId, type);
//            FundsDailyCost diffRecord = new FundsDailyCost();
//            diffRecord.setMainId(previousDiffRecord.getMainId());
//            diffRecord.setInterestDate(interestDate.plusDays(1));
//            diffRecord.setFinancingId(financingId);
//            diffRecord.setType(type);
//            diffRecord.setItemText(DIFF_ITEM_TEXT);
//            diffRecord.setFinancingAmount(newRecord.getFinancingAmount());
//            diffRecord.setFinancingCost(financingCost+interestAmount);
//            diffRecord.setTotalCapitalCost(newRecord.getTotalCapitalCost() - previousRecord.getTotalCapitalCost());
//            diffRecord.setTotalCapitalCostAfterTax(newRecord.getTotalCapitalCostAfterTax() - previousRecord.getTotalCapitalCostAfterTax());
//            if (Objects.nonNull(previousDiffRecord)) {
//                diffRecord.setId(previousDiffRecord.getId());
//                this.updateById(diffRecord);
//                MonthlyManageUpdateEvent.DataObject dataObject = getDataObject(diffRecord.getMainId(), interestDate);
//                ApplicationContextUtil.getApplicationContext().publishEvent(dataObject);
//            } else {
//                this.save(diffRecord);
//            }
//        }
//    }
//
//    public void doDirectFundsCalculate(FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo, LocalDate interestDate) {
//        double taxRate = 0.06;
//        Long financingId = fundDirectFinancingBaseInfo.getId();
//        //期初余额
//        FundsDailyCost beginningDailyCost = getBeginningDailyCost(financingId, "ZR");
//        if (!interestDate.isAfter(beginningDailyCost.getInterestDate())
//                && !interestDate.isEqual(beginningDailyCost.getInterestDate().plusDays(1))) {
//            return;
//        }
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        // 当日的成本计提
//        FundsDailyCost existRecord = null;
//        existRecord = getConfirmedSpecificDailyCost(financingId, interestDate, "ZR");
//        FundsDailyCost tempExistRecord = getSpecificDailyCost(financingId, interestDate, "DK");;
//        if (tempExistRecord != null) {
//            existRecord = tempExistRecord;
//        }
//        // 前一日的成本计提
//        FundsDailyCost previousRecord = getSpecificDailyCost(financingId, interestDate.minusDays(1), "ZR");
//
//        String itemText = formatter.format(interestDate);
//        Long financingAmount = 0L;
//        Long principleAmount = 0L;
//        Long interestAmount = 0L;
//        int financingRate = fundDirectFinancingBaseInfo.getAverageCouponRate().intValue();
//        int dailyRate = 0;
//        Long totalCapitalCost = 0L;
//        Long totalCapitalCostAfterTax = 0L;
//        Long financingCost = 0L;
//        String type = "ZR";
//        String propertyType = null;
//
//        if (previousRecord == null && interestDate.isEqual(beginningDailyCost.getInterestDate().plusDays(1))) {
//            financingAmount = beginningDailyCost.getFinancingAmount();
//            totalCapitalCost = beginningDailyCost.getTotalCapitalCost();
//            totalCapitalCostAfterTax = beginningDailyCost.getTotalCapitalCostAfterTax();
//        } else if (previousRecord != null){
//            if (YesOrNoNumberEnum.YES.getCode().equals(previousRecord.getIsConfirmed())
//                    && existRecord != null) {
//                if (existRecord.getItemText().equalsIgnoreCase(DIFF_ITEM_TEXT)) {
//                    financingAmount = existRecord.getFinancingAmount() + existRecord.getPrincipleAmount();
//                    totalCapitalCost = previousRecord.getTotalCapitalCost() + existRecord.getTotalCapitalCost();
//                    totalCapitalCostAfterTax = previousRecord.getTotalCapitalCostAfterTax() + existRecord.getTotalCapitalCostAfterTax();
//                } else {
//                    financingAmount = existRecord.getFinancingAmount() + existRecord.getPrincipleAmount();
//                    totalCapitalCost = existRecord.getTotalCapitalCost() - existRecord.getFinancingCost();
//                    totalCapitalCostAfterTax = existRecord.getTotalCapitalCostAfterTax() - existRecord.getFinancingCost();
//                }
//            } else {
//                financingAmount = previousRecord.getFinancingAmount();
//                totalCapitalCost = previousRecord.getTotalCapitalCost();
//                totalCapitalCostAfterTax = previousRecord.getTotalCapitalCostAfterTax();
//            }
//        }
//        FundDirectFinancingRepayActual fundDirectFinancingRepayActual = getDirectRepayActual(financingId, interestDate);
//        if (fundDirectFinancingRepayActual != null) {
//            principleAmount = fundDirectFinancingRepayActual.getPrincipleAmount();
//            interestAmount = fundDirectFinancingRepayActual.getInterestAmount();
//            if (Objects.nonNull(principleAmount)) {
//                financingAmount -= principleAmount;
//            }
//        }
//
//        LambdaQueryWrapper<FundDirectFinancingPledgeInfo> query = Wrappers.lambdaQuery();
//        query.eq(FundDirectFinancingPledgeInfo::getFinancingId, financingId)
//                .eq(FundDirectFinancingPledgeInfo::getIsPledge, 1);
//        List<FundDirectFinancingPledgeInfo> fundDirectFinancingPledgeInfoList = fundDirectFinancingPledgeInfoService.list(query);
//        if (fundDirectFinancingPledgeInfoList != null && !fundDirectFinancingPledgeInfoList.isEmpty()) {
//            fundDirectFinancingPledgeInfoList = fundDirectFinancingPledgeInfoList.stream()
//                    .sorted(Comparator.comparing(FundDirectFinancingPledgeInfo::getContractAmount).reversed()).collect(Collectors.toList());
//            if (!fundDirectFinancingPledgeInfoList.isEmpty()) {
//                FundDirectFinancingPledgeInfo fundDirectFinancingPledgeInfo = fundDirectFinancingPledgeInfoList.get(0);
//                ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(fundDirectFinancingPledgeInfo.getContractId());
//                if (contractBaseInfo != null) {
//                    if (ProjectBizType.BL.name().equalsIgnoreCase(contractBaseInfo.getBizType())) {
//                        propertyType = ProjectBizType.BL.name();
//                    } else if (ProjectBizType.ZL.name().equalsIgnoreCase(contractBaseInfo.getBizType())
//                            && LeaseType.zhi_zu.name().equalsIgnoreCase(contractBaseInfo.getLeaseType())) {
//                        propertyType = LeaseType.zhi_zu.name();
//                    } else if (ProjectBizType.ZL.name().equalsIgnoreCase(contractBaseInfo.getBizType())
//                            && !LeaseType.zhi_zu.name().equalsIgnoreCase(contractBaseInfo.getLeaseType())){
//                        propertyType= contractBaseInfo.getLeaseType();
//                    } else {
//                        propertyType = contractBaseInfo.getBizType();
//                    }
//                }
//            }
//        }
//
//        if (financingRate > 0) {
//            dailyRate = calculateDailyRate(financingRate).intValue();
//            financingCost = this.calculateInterest(financingRate, financingAmount);
//            if (Objects.nonNull(interestAmount)) {
//                financingCost -= interestAmount;
//            }
//            totalCapitalCost += financingCost;
//            //1.不含税利息：租金表中的利息总额/ （1+税率）， 保留2位小数
//            totalCapitalCostAfterTax = NumberUtil.div(totalCapitalCost, new BigDecimal(String.valueOf(1 + taxRate)), 2, RoundingMode.HALF_UP).longValue();
//        }
//
//        LocalDate lastDayOfThisMonth = interestDate.with(TemporalAdjusters.lastDayOfMonth());
//        FundsDailyCost newRecord = new FundsDailyCost();
//        newRecord.setDailyRate(dailyRate);
//        newRecord.setFinancingCost(financingCost + interestAmount);
//        newRecord.setInterestAmount(interestAmount);
//        newRecord.setFinancingRate(financingRate);
//        newRecord.setFinancingAmount(financingAmount);
//        newRecord.setFinancingId(financingId);
//        newRecord.setPrincipleAmount(principleAmount);
//        newRecord.setInterestDate(interestDate);
//        newRecord.setType(type);
//        newRecord.setPropertyType(propertyType);
//        newRecord.setItemText(itemText);
//        newRecord.setTotalCapitalCost(totalCapitalCost);
//        newRecord.setTotalCapitalCostAfterTax(totalCapitalCostAfterTax);
//        if (Objects.nonNull(existRecord)
//                && YesOrNoNumberEnum.NO.getCode().equals(existRecord.getIsConfirmed())
//                && !DIFF_ITEM_TEXT.equalsIgnoreCase(existRecord.getItemText())) {
//            newRecord.setId(existRecord.getId());
//            this.updateById(newRecord);
//            MonthlyManageUpdateEvent.DataObject dataObject = getDataObject(newRecord.getMainId(), interestDate);
//            ApplicationContextUtil.getApplicationContext().publishEvent(dataObject);
//        } else if ((existRecord == null && previousRecord != null)
//                || !newRecord.getTotalCapitalCost().equals(existRecord.getTotalCapitalCost())){
//            this.save(newRecord);
//        }
//        if (Objects.nonNull(existRecord)
//                && YesOrNoNumberEnum.YES.getCode().equals(existRecord.getIsConfirmed())
//                && lastDayOfThisMonth.equals(existRecord.getInterestDate())
//                && !newRecord.getTotalCapitalCost().equals(existRecord.getTotalCapitalCost())) {
//            FundsDailyCost previousDiffRecord = getDiffDailyCost(financingId, type);
//            FundsDailyCost diffRecord = new FundsDailyCost();
//            diffRecord.setInterestDate(interestDate.plusDays(1));
//            diffRecord.setFinancingId(financingId);
//            diffRecord.setType(type);
//            diffRecord.setItemText(DIFF_ITEM_TEXT);
//            diffRecord.setFinancingCost(financingCost+interestAmount);
//            diffRecord.setTotalCapitalCost(newRecord.getTotalCapitalCost() - existRecord.getTotalCapitalCost());
//            diffRecord.setTotalCapitalCostAfterTax(newRecord.getTotalCapitalCostAfterTax() - existRecord.getTotalCapitalCostAfterTax());
//            if (Objects.nonNull(previousDiffRecord)) {
//                diffRecord.setId(previousDiffRecord.getId());
//                this.updateById(diffRecord);
//                MonthlyManageUpdateEvent.DataObject dataObject = getDataObject(diffRecord.getMainId(), interestDate);
//                ApplicationContextUtil.getApplicationContext().publishEvent(dataObject);
//            } else {
//                this.save(diffRecord);
//            }
//        } else if (Objects.nonNull(previousRecord)
//                && YesOrNoNumberEnum.YES.getCode().equals(previousRecord.getIsConfirmed())
//                && lastDayOfThisMonth.equals(previousRecord.getInterestDate())
//                && !newRecord.getTotalCapitalCost().equals(previousRecord.getTotalCapitalCost())) {
//            FundsDailyCost previousDiffRecord = getDiffDailyCost(financingId, type);
//            FundsDailyCost diffRecord = new FundsDailyCost();
//            diffRecord.setInterestDate(interestDate.plusDays(1));
//            diffRecord.setFinancingId(financingId);
//            diffRecord.setType(type);
//            diffRecord.setItemText(DIFF_ITEM_TEXT);
//            diffRecord.setFinancingAmount(newRecord.getFinancingAmount());
//            diffRecord.setFinancingCost(financingCost+interestAmount);
//            diffRecord.setTotalCapitalCost(newRecord.getTotalCapitalCost() - previousRecord.getTotalCapitalCost());
//            diffRecord.setTotalCapitalCostAfterTax(newRecord.getTotalCapitalCostAfterTax() - previousRecord.getTotalCapitalCostAfterTax());
//            if (Objects.nonNull(previousDiffRecord)) {
//                diffRecord.setId(previousDiffRecord.getId());
//                this.updateById(diffRecord);
//                MonthlyManageUpdateEvent.DataObject dataObject = getDataObject(diffRecord.getMainId(), interestDate);
//                ApplicationContextUtil.getApplicationContext().publishEvent(dataObject);
//            } else {
//                this.save(diffRecord);
//            }
//        }
//    }



//    public FundsDailyCost getSpecificDailyCost(Long financingId, LocalDate targetDate, String type) {
//        LambdaQueryWrapper<FundsDailyCost> query = Wrappers.lambdaQuery();
//        query.eq(FundsDailyCost::getFinancingId, financingId);
//        query.eq(FundsDailyCost::getInterestDate, targetDate);
//        query.eq(FundsDailyCost::getType, type);
//        query.ne(FundsDailyCost::getItemText, BEGINNING_ITEM_TEXT);
//        query.orderByDesc(FundsDailyCost::getId);
//        //query.ne(FundsDailyCost::getItemText, DIFF_ITEM_TEXT);
//        query.last(StringUtil.mysqlLimitOne());
//        return this.getOne(query);
//    }

//    public FundsDailyCost getConfirmedSpecificDailyCost(Long financingId, LocalDate targetDate, String type) {
//        LambdaQueryWrapper<FundsDailyCost> query = Wrappers.lambdaQuery();
//        query.eq(FundsDailyCost::getFinancingId, financingId);
//        query.eq(FundsDailyCost::getInterestDate, targetDate);
//        query.eq(FundsDailyCost::getType, type);
//        query.eq(FundsDailyCost::getIsConfirmed, YesOrNoNumberEnum.YES.getCode());
//        query.ne(FundsDailyCost::getItemText, BEGINNING_ITEM_TEXT);
//        //query.ne(FundsDailyCost::getItemText, DIFF_ITEM_TEXT);
//        query.orderByDesc(FundsDailyCost::getId);
//        query.last(StringUtil.mysqlLimitOne());
//        return this.getOne(query);
//    }
//
//    public FundsDailyCost getBeginningDailyCost(Long financingId, String type) {
//        LambdaQueryWrapper<FundsDailyCost> query = Wrappers.lambdaQuery();
//        query.eq(FundsDailyCost::getFinancingId, financingId);
//        query.eq(FundsDailyCost::getType, type);
//        query.eq(FundsDailyCost::getItemText, BEGINNING_ITEM_TEXT);
//        query.last(StringUtil.mysqlLimitOne());
//        return this.getOne(query);
//    }
//
//    public FundsDailyCost getDiffDailyCost(Long financingId, String type) {
//        LambdaQueryWrapper<FundsDailyCost> query = Wrappers.lambdaQuery();
//        query.eq(FundsDailyCost::getFinancingId, financingId);
//        query.eq(FundsDailyCost::getType, type);
//        query.ne(FundsDailyCost::getItemText, BEGINNING_ITEM_TEXT);
//        query.eq(FundsDailyCost::getItemText, DIFF_ITEM_TEXT);
//        query.last(StringUtil.mysqlLimitOne());
//        return this.getOne(query);
//    }
//
//    public FundFinancingRepayActual getRepayActual(Long financingId, LocalDate targetDate) {
//        LambdaQueryWrapper<FundFinancingRepayActual> query = Wrappers.lambdaQuery();
//        query.eq(FundFinancingRepayActual::getFinancingId, financingId);
//        query.eq(FundFinancingRepayActual::getRepayDate, targetDate);
//        return fundFinancingRepayActualService.getOne(query);
//    }
//
//    public FundDirectFinancingRepayActual getDirectRepayActual(Long financingId, LocalDate targetDate) {
//        LambdaQueryWrapper<FundDirectFinancingRepayActual> query = Wrappers.lambdaQuery();
//        query.eq(FundDirectFinancingRepayActual::getFinancingId, financingId);
//        query.eq(FundDirectFinancingRepayActual::getRepayDate, targetDate);
//        return fundDirectFinancingRepayActualService.getOne(query);
//    }
//
//    private long calculateInterest(Integer ftp, long occupy) {
//        return Util.mithrasLongDecimalTwo(BigDecimal.valueOf(ftp).divide(BigDecimal.valueOf(1000000 * 360), 20, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(occupy)).longValue());
//    }
//
//    private Long calculateDailyRate(Integer ftp) {
//        return Util.mithrasLongDecimalTwo(BigDecimal.valueOf(ftp).divide(BigDecimal.valueOf(360), 20, RoundingMode.HALF_UP).longValue());
//    }
//
//    private MonthlyManageUpdateEvent.DataObject getDataObject(Long recordId, LocalDate interestDate) {
//        MonthlyManageUpdateEvent.DataObject dataObject = new MonthlyManageUpdateEvent.DataObject();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
//        String yearAndMonth = formatter.format(interestDate);
//        dataObject.setModelType(MonthlyModuleTypeEnum.COST.name());
//        dataObject.setRecordId(recordId);
//        dataObject.setYearAndMonth(yearAndMonth);
//        return dataObject;
//    }

    public List<FundsDailyCost> getByInterestDate(LocalDate from, LocalDate to) {
        LambdaQueryWrapper<FundsDailyCost> query = Wrappers.lambdaQuery();
        query.ge(FundsDailyCost::getInterestDate, from);
        query.le(FundsDailyCost::getInterestDate, to);
        query.ne(FundsDailyCost::getItemText, BEGINNING_ITEM_TEXT);
        return this.baseMapper.selectList(query);
    }

}
