package cn.zswltech.mithras.liquidity.service.cal.account;

import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.liquidity.enums.LiquidityIndexType;
import cn.zswltech.mithras.liquidity.model.AccountBalanceBaseInfo;
import cn.zswltech.mithras.liquidity.service.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.liquidity.service.cal.bo.LiquidityAccountManualCalculatorBo;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 *  结余受限（预估）
 *
 *  字段初始默认逻辑：
 *  非监管户=上一日受限，可修改
 *  监管户=结余（预估），可修改
 *  @author chenyifei
 *  @since 2024/12/16
 */
@Component
public class EstimateBalanceLimitAmountCalculator extends AbstractLiquidityCalculator<LiquidityAccountManualCalculatorBo> {

    @Override
    public void calculate(Object obj, LiquidityAccountManualCalculatorBo bo) {
        Long result = 0L;
        AccountBalanceBaseInfo accountBalanceBeforeOneDay = bo.getAccountBalanceBeforeDay();
        if(accountBalanceBeforeOneDay != null) {
            result = Optional.ofNullable(Optional.ofNullable(accountBalanceBeforeOneDay.getEstimateBalanceLimitEditAmount()).orElse(accountBalanceBeforeOneDay.getEstimateBalanceLimitAmount())).orElse(0L);
        }

        ReflectUtil.setFieldValue(obj, indexName(),result);
    }

    @Override
    public LiquidityIndexType model() {
        return LiquidityIndexType.MANUAL_TRIGGER;
    }

    @Override
    public String indexName() {
        return "estimateBalanceLimitAmount";
    }
}
