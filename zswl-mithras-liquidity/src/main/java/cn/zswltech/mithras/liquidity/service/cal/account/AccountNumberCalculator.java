package cn.zswltech.mithras.liquidity.service.cal.account;

import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.liquidity.snapshot.LiquidityBankAccountSnapshot;
import cn.zswltech.mithras.liquidity.enums.LiquidityIndexType;
import cn.zswltech.mithras.liquidity.service.LiquidityIndicatorHolder;
import cn.zswltech.mithras.liquidity.service.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.liquidity.service.cal.bo.LiquidityAccountCalculatorBo;
import org.springframework.stereotype.Component;

/**
 * 银行账号
 *
 * @author chenyifei
 * @since 2024/12/17
 */
@Component
public class AccountNumberCalculator extends AbstractLiquidityCalculator<LiquidityAccountCalculatorBo> {
    @Override
    public void calculate(Object obj, LiquidityAccountCalculatorBo bo) {
        LiquidityBankAccountSnapshot baseDataBankAccount = LiquidityIndicatorHolder.BASE_DATA_BANK_ACCOUNT.get(bo.getAccountBankId());
        if(baseDataBankAccount != null) {
            ReflectUtil.setFieldValue(obj, indexName(), baseDataBankAccount.getAccountNumber().replaceAll(" ",""));
        }
    }

    @Override
    public LiquidityIndexType model() {
        return LiquidityIndexType.ACCOUNT_BALANCE;
    }

    @Override
    public String indexName() {
        return "accountNumber";
    }
}
