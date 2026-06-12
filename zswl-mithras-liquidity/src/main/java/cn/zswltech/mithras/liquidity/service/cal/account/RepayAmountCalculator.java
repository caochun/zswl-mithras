package cn.zswltech.mithras.liquidity.service.cal.account;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.fund.enums.financing.FinancingTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingAccountTypeEnum;
import cn.zswltech.mithras.liquidity.enums.LiquidityIndexType;
import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingRepayActual;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingRepayActual;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundReceiptFlowPlan;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.liquidity.model.FundFinancingAccountSetting;
import cn.zswltech.mithras.liquidity.service.LiquidityIndicatorHolder;
import cn.zswltech.mithras.liquidity.service.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.liquidity.service.cal.bo.LiquidityAccountCalculatorBo;
import cn.zswltech.mithras.foundation.util.LongUtil;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *  还本付息
 *
 *  数据范围：融资合同状态=起息，应付日=当天，我司还款账户=此账户（间融）
 *  还本付息金额之和（直融+间融）
 *  备注：
 *  1. 不考虑节假日影响
 *  2. 直融的我司还款账户默认为4595基本户
 *  3. 需按“预测参数配置表”的回款账户统计（需要区分本金和利息）
 *  4. 如“预测参数配置表”中“是否模拟结清”选择为“是”，则模拟结清日后的还本付息计划都不再取数，按照模拟结清日和金额计算回款
 *  @author chenyifei
 *  @since 2024/12/16
 */
@Component
public class RepayAmountCalculator extends AbstractLiquidityCalculator<LiquidityAccountCalculatorBo> {

    @Override
    public void calculate(Object obj, LiquidityAccountCalculatorBo bo) {
        ReflectUtil.setFieldValue(obj, indexName(), inDirect(bo) + direct(bo));
    }

    private long inDirect(LiquidityAccountCalculatorBo bo) {
        List<FundFinancingAccountSetting> financingAccountSettingList = LiquidityIndicatorHolder.FUND_FINANCING_ACCOUNT_SETTING.get(bo.getAccountBankId());
        List<FundReceiptFlowPlan> receiptFlowPlanList = LiquidityIndicatorHolder.FUND_RECEIPT_FLOW_PLAN.get(bo.getLocalDate());
        Map<Long, Long> inDirectReceiptIdMap = LiquidityIndicatorHolder.FUND_RECEIPT_REPAY_BASE_INFO.values().stream().filter(f -> f.getFinancingType() == null)
                .collect(Collectors.toMap(FundReceiptRepayBaseInfo::getId, FundReceiptRepayBaseInfo::getFinancingId));
//        List<FundFinancingRepayActual> repayActualList = LiquidityIndicatorHolder.FUND_FINANCING_REPAY_ACTUAL.get(bo.getLocalDate());
        // 筛选应付日与账户
        if(CollectionUtil.isNotEmpty(financingAccountSettingList)){
            Map<Long, FundFinancingAccountSetting> settingMap = financingAccountSettingList.stream().filter(f -> f.getFinancingType() == null)
                    .collect(Collectors.toMap(FundFinancingAccountSetting::getFinancingId, Function.identity(), (m1,m2) -> m1));

            // 若设置的结清日就为当日, 将结清金额累加到这一期
            long settleAmountSum = settingMap.values().stream().filter(f -> BooleanUtil.isTrue(f.getSimulateSettle()) && bo.getLocalDate().compareTo(f.getSettleTime()) == 0)
                    .mapToLong(f -> LongUtil.null2zero(f.getSettleAmount())).sum();
            long repaySum = 0L;
            if(CollectionUtil.isNotEmpty(financingAccountSettingList) && CollectionUtil.isNotEmpty(receiptFlowPlanList)) {
                repaySum = receiptFlowPlanList.stream().mapToLong(m -> {
                    Long financingId = inDirectReceiptIdMap.get(m.getReceiptRepayId());
                    if(financingId == null) {
                        return 0L;
                    }
                    FundFinancingAccountSetting setting = settingMap.get(financingId);
                    if (setting == null) {
                        return 0;
                    }
                    Long amount = LongUtil.null2zero(m.getPrincipalAmount()) + LongUtil.null2zero(m.getInterestAmount());
                    // 如“预测参数配置表”中“是否模拟结清”选择为“是”，则模拟结清日后的还本付息计划都不再取数，按照模拟结清日和金额计算回款
                    if (BooleanUtil.isTrue(setting.getSimulateSettle())) {
                        if (m.getCashFlowDate().isAfter(setting.getSettleTime())) {
                            return 0L;
                        }
                        if (m.getCashFlowDate().compareTo(setting.getSettleTime()) == 0) {
                            // 设置的结清日就为当日, 将结清金额累加到这一期
                            return amount + LongUtil.null2zero(setting.getSettleAmount());
                        }

                    }
                    if (Objects.equals(setting.getAccountCategory(), FundFinancingAccountTypeEnum.REPAY_PRINCIPAL.name())) {
                        amount = m.getPrincipalAmount();
                    } else if (Objects.equals(setting.getAccountCategory(), FundFinancingAccountTypeEnum.REPAY_INTEREST.name())) {
                        amount = m.getInterestAmount();
                    }
                    return LongUtil.null2zero(amount);
                }).sum();
            }
            return settleAmountSum + repaySum;

        }
        return 0;
    }


    private long direct(LiquidityAccountCalculatorBo bo) {
        // 直融
        if(Objects.equals(bo.getAccountBankId(), LiquidityIndicatorHolder.DEFAULT_ACCOUNT.getId())){
            List<FundFinancingAccountSetting> settingList = LiquidityIndicatorHolder.FUND_FINANCING_ACCOUNT_SETTING.get(bo.getAccountBankId());
            List<FundReceiptFlowPlan> receiptFlowPlanList = LiquidityIndicatorHolder.FUND_RECEIPT_FLOW_PLAN.get(bo.getLocalDate());
//            List<FundDirectFinancingRepayActual> repayActualList = LiquidityIndicatorHolder.FUND_DIRECT_FINANCING_REPAY_ACTUAL.get(bo.getLocalDate());
            Map<Long, Long> directReceiptIdMap = LiquidityIndicatorHolder.FUND_RECEIPT_REPAY_BASE_INFO.values().stream().filter(f -> Objects.equals(f.getFinancingType(), FinancingTypeEnum.DIRECT.name()))
                    .collect(Collectors.toMap(FundReceiptRepayBaseInfo::getId, FundReceiptRepayBaseInfo::getFinancingId));
            if(CollectionUtil.isNotEmpty(settingList)){
                Map<Long, FundFinancingAccountSetting> settingMap = settingList.stream().filter(f -> Objects.equals(f.getFinancingType(), FinancingTypeEnum.DIRECT.name()))
                        .collect(Collectors.toMap(FundFinancingAccountSetting::getFinancingId, Function.identity(), (m1,m2) -> m1));
                // 若设置的结清日就为当日, 将结清金额累加到这一期
                long settleAmountSum = settingMap.values().stream().filter(f -> BooleanUtil.isTrue(f.getSimulateSettle()) && bo.getLocalDate().compareTo(f.getSettleTime()) == 0)
                        .mapToLong(f -> LongUtil.null2zero(f.getSettleAmount())).sum();
                long repaySum = 0;
                if(CollectionUtil.isNotEmpty(receiptFlowPlanList)) {
                    repaySum = receiptFlowPlanList.stream().mapToLong(m -> {
                        Long financingId = directReceiptIdMap.get(m.getReceiptRepayId());
                        FundFinancingAccountSetting setting = settingMap.get(financingId);
                        if (setting == null) {
                            return 0;
                        }
                        Long result = LongUtil.null2zero(m.getPrincipalAmount()) + LongUtil.null2zero(m.getInterestAmount());
                        // 如“预测参数配置表”中“是否模拟结清”选择为“是”，则模拟结清日后的还本付息计划都不再取数，按照模拟结清日和金额计算回款
                        if (BooleanUtil.isTrue(setting.getSimulateSettle())) {
                            if (m.getCashFlowDate().isAfter(setting.getSettleTime())) {
                                return 0L;
                            }
                            if (m.getCashFlowDate().compareTo(setting.getSettleTime()) == 0) {
                                // 设置的结清日就为当日, 将结清金额累加到这一期
                                return result + LongUtil.null2zero(setting.getSettleAmount());
                            }
                        }
                        return result;
                    }).sum();
                }
                return settleAmountSum + repaySum;
            }
        }
        return 0;
    }

    @Override
    public LiquidityIndexType model() {
        return LiquidityIndexType.ACCOUNT_BALANCE;
    }

    @Override
    public String indexName() {
        return "repayAmount";
    }
}
