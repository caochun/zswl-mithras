package cn.zswltech.mithras.liquiditymanage.service.cal.account;

import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.liquiditymanage.enums.LiquidityIndexType;
import cn.zswltech.mithras.liquiditymanage.service.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.liquiditymanage.service.cal.bo.LiquidityAccountCalculatorBo;
import org.springframework.stereotype.Component;

/**
 * 账户id
 *
 * @author chenyifei
 * @since 2024/12/17
 */
@Component
public class AccountIdCalculator extends AbstractLiquidityCalculator<LiquidityAccountCalculatorBo> {
    @Override
    public void calculate(Object obj, LiquidityAccountCalculatorBo bo) {
        ReflectUtil.setFieldValue(obj, indexName(), bo.getAccountBankId());
    }

    @Override
    public LiquidityIndexType model() {
        return LiquidityIndexType.ACCOUNT_BALANCE;
    }

    @Override
    public String indexName() {
        return "accountId";
    }
}
