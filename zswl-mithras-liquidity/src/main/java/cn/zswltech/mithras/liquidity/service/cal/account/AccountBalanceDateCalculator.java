package cn.zswltech.mithras.liquidity.service.cal.account;

import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.liquidity.enums.LiquidityIndexType;
import cn.zswltech.mithras.liquidity.service.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.liquidity.service.cal.bo.LiquidityAccountCalculatorBo;
import org.springframework.stereotype.Component;

/**
 * 时间
 *
 * @author chenyifei
 * @since 2024/12/17
 */
@Component
public class AccountBalanceDateCalculator extends AbstractLiquidityCalculator<LiquidityAccountCalculatorBo> {
    @Override
    public void calculate(Object obj, LiquidityAccountCalculatorBo bo) {
        ReflectUtil.setFieldValue(obj, indexName(), bo.getLocalDate());
    }

    @Override
    public LiquidityIndexType model() {
        return LiquidityIndexType.ACCOUNT_BALANCE;
    }

    @Override
    public String indexName() {
        return "date";
    }
}
