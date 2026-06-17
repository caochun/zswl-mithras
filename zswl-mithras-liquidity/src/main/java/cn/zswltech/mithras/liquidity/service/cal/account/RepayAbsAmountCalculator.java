package cn.zswltech.mithras.liquidity.service.cal.account;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.liquidity.bo.LiquidityDirectFinancingSnapshot;
import cn.zswltech.mithras.liquidity.bo.LiquidityDirectFinancingRepayActualSnapshot;
import cn.zswltech.mithras.liquidity.enums.LiquidityIndexType;
import cn.zswltech.mithras.liquidity.service.LiquidityIndicatorHolder;
import cn.zswltech.mithras.liquidity.service.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.liquidity.service.cal.bo.LiquidityAccountCalculatorBo;
import cn.zswltech.mithras.foundation.util.LongUtil;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 *  还本付息ABS
 *
 *  数据范围：融资合同类型=ABS\ABN,状态=起息，应付日=当天
 *  还本付息金额之和（直融+间融）
 *  备注：
 *  1. 不考虑节假日影响
 *  2. 直融的我司还款账户默认为4595基本户
 *  3. 此字段不在前台页面展示
 *  @author chenyifei
 *  @since 2024/12/16
 */
@Component
public class RepayAbsAmountCalculator extends AbstractLiquidityCalculator<LiquidityAccountCalculatorBo> {

    private static final List<String> ABS_DIRECT_FINANCING_TYPES = Arrays.asList("ABS", "ABN");

    @Override
    public void calculate(Object obj, LiquidityAccountCalculatorBo bo) {
        long result = 0;
        if(Objects.equals(bo.getAccountBankId(), LiquidityIndicatorHolder.DEFAULT_ACCOUNT.getId())) {
            List<LiquidityDirectFinancingRepayActualSnapshot> repayActualList = LiquidityIndicatorHolder.FUND_DIRECT_FINANCING_REPAY_ACTUAL.get(bo.getLocalDate());
            if (CollectionUtil.isNotEmpty(repayActualList)) {
                result = repayActualList.stream().filter(f -> {
                    LiquidityDirectFinancingSnapshot directFinancingBaseInfo = LiquidityIndicatorHolder.FUND_DIRECT_FINANCING_BASE_INFO.get(f.getFinancingId());
                    return directFinancingBaseInfo != null && ABS_DIRECT_FINANCING_TYPES.contains(directFinancingBaseInfo.getDirectFinancingType());
                }).mapToLong(m -> LongUtil.null2zero(m.getRepayAmount())).sum();
            }
        }
        ReflectUtil.setFieldValue(obj, indexName(), result);


    }

    @Override
    public LiquidityIndexType model() {
        return LiquidityIndexType.ACCOUNT_BALANCE;
    }

    @Override
    public String indexName() {
        return "repayAbsAmount";
    }
}
