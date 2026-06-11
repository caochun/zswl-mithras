package cn.zswltech.mithras.liquidity.service.cal.account;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.fund.enums.DirectFinancingType;
import cn.zswltech.mithras.fund.enums.financing.FinancingTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingAccountTypeEnum;
import cn.zswltech.mithras.liquidity.enums.LiquidityIndexType;
import cn.zswltech.mithras.fund.directfinancing.mapper.model.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.fund.directfinancing.mapper.model.FundDirectFinancingRepayActual;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingPayAccount;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingRepayActual;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptFlowPlan;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.liquidity.service.LiquidityIndicatorHolder;
import cn.zswltech.mithras.liquidity.service.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.liquidity.service.cal.bo.LiquidityAccountCalculatorBo;
import cn.zswltech.mithras.foundation.util.LongUtil;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 还本付息（非ABS）
 *
 * 数据范围：融资合同类型不等于ABS\ABN,状态=起息，应付日=当天，我司还款账户=此账户（间融）
 * 还本付息金额之和（直融+间融）
 * 备注：
 * 1. 不考虑节假日影响
 * 2. 直融的我司还款账户默认为4595基本户
 * 3. 此字段不在前台页面展示
 *
 * @author chenyifei
 * @since 2024/12/16
 */
@Component
public class RepayNoAbsAmountCalculator extends AbstractLiquidityCalculator<LiquidityAccountCalculatorBo> {

    @Override
    public void calculate(Object obj, LiquidityAccountCalculatorBo bo) {
        ReflectUtil.setFieldValue(obj, indexName(), inDirect(bo) + direct(bo));
    }

    private long inDirect(LiquidityAccountCalculatorBo bo) {
        List<FundFinancingPayAccount> fundFinancingPayAccountList = LiquidityIndicatorHolder.FUND_FINANCING_PAY_ACCOUNT.get(bo.getAccountBankId());
        List<FundReceiptFlowPlan> receiptFlowPlanList = LiquidityIndicatorHolder.FUND_RECEIPT_FLOW_PLAN.get(bo.getLocalDate());
        Map<Long, Long> inDirectReceiptIdMap = LiquidityIndicatorHolder.FUND_RECEIPT_REPAY_BASE_INFO.values().stream().filter(f -> f.getFinancingType() == null)
                .collect(Collectors.toMap(FundReceiptRepayBaseInfo::getId, FundReceiptRepayBaseInfo::getFinancingId));
//        List<FundFinancingRepayActual> repayActualList = LiquidityIndicatorHolder.FUND_FINANCING_REPAY_ACTUAL.get(bo.getLocalDate());
        // 筛选应付日与账户
        if (CollectionUtil.isNotEmpty(fundFinancingPayAccountList) && CollectionUtil.isNotEmpty(receiptFlowPlanList)) {
            Map<Long, FundFinancingPayAccount> accountMap = fundFinancingPayAccountList.stream().collect(Collectors.toMap(FundFinancingPayAccount::getFinancingId, Function.identity(), (m1,m2) -> m1));
            return receiptFlowPlanList.stream().mapToLong(m -> {
                FundFinancingPayAccount payAccount = accountMap.get(Optional.ofNullable(inDirectReceiptIdMap.get(m.getReceiptRepayId())).orElse(-1L));
                if(payAccount == null){
                    return 0L;
                }
                Long amount = LongUtil.null2zero(m.getPrincipalAmount()) + LongUtil.null2zero(m.getInterestAmount());
                if(Objects.equals(payAccount.getAccountCategory(), FundFinancingAccountTypeEnum.REPAY_PRINCIPAL.name())){
                    amount = m.getPrincipalAmount();
                }else if(Objects.equals(payAccount.getAccountCategory(), FundFinancingAccountTypeEnum.REPAY_INTEREST.name())){
                    amount = m.getInterestAmount();
                }
                return LongUtil.null2zero(amount);
            }).sum();
        }
        return 0;
    }


    private long direct(LiquidityAccountCalculatorBo bo) {
        // 直融
        if (Objects.equals(bo.getAccountBankId(), LiquidityIndicatorHolder.DEFAULT_ACCOUNT.getId())) {
//            List<FundDirectFinancingRepayActual> repayActualList = LiquidityIndicatorHolder.FUND_DIRECT_FINANCING_REPAY_ACTUAL.get(bo.getLocalDate());
            List<FundReceiptFlowPlan> receiptFlowPlanList = LiquidityIndicatorHolder.FUND_RECEIPT_FLOW_PLAN.get(bo.getLocalDate());
            Map<Long, Long> directReceiptIdMap = LiquidityIndicatorHolder.FUND_RECEIPT_REPAY_BASE_INFO.values().stream().filter(f -> Objects.equals(f.getFinancingType(), FinancingTypeEnum.DIRECT.name()))
                    .collect(Collectors.toMap(FundReceiptRepayBaseInfo::getId, FundReceiptRepayBaseInfo::getFinancingId));
            if (CollectionUtil.isNotEmpty(receiptFlowPlanList)) {
                return receiptFlowPlanList.stream().filter(f -> {
                        FundDirectFinancingBaseInfo directFinancingBaseInfo = LiquidityIndicatorHolder.FUND_DIRECT_FINANCING_BASE_INFO.get(Optional.ofNullable(directReceiptIdMap.get(f.getReceiptRepayId())).orElse(-1L));
                        if(directFinancingBaseInfo == null){
                            return false;
                        }
                        return !Arrays.asList(DirectFinancingType.ABS.name(), DirectFinancingType.ABN.name()).contains(directFinancingBaseInfo.getDirectFinancingType());
                    }).mapToLong(m -> LongUtil.null2zero(m.getPrincipalAmount()) + LongUtil.null2zero(m.getInterestAmount())).sum();
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
        return "repayNoAbsAmount";
    }
}
