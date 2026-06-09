package cn.zswltech.mithras.liquiditymanage.service.cal.account;

import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.liquiditymanage.enums.LiquidityIndexType;
import cn.zswltech.mithras.basedata.mapper.model.BaseDataBankAccount;
import cn.zswltech.mithras.liquiditymanage.service.LiquidityIndicatorHolder;
import cn.zswltech.mithras.liquiditymanage.service.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.liquiditymanage.service.cal.bo.LiquidityAccountCalculatorBo;
import org.springframework.stereotype.Component;

/**
 * 开户银行
 *
 * @author chenyifei
 * @since 2024/12/17
 */
@Component
public class AccountBankCalculator extends AbstractLiquidityCalculator<LiquidityAccountCalculatorBo> {
    @Override
    public void calculate(Object obj, LiquidityAccountCalculatorBo bo) {
        BaseDataBankAccount baseDataBankAccount = LiquidityIndicatorHolder.BASE_DATA_BANK_ACCOUNT.get(bo.getAccountBankId());
        if(baseDataBankAccount != null) {
            ReflectUtil.setFieldValue(obj, indexName(), baseDataBankAccount.getAccountBank());
        }
    }

    @Override
    public LiquidityIndexType model() {
        return LiquidityIndexType.ACCOUNT_BALANCE;
    }

    @Override
    public String indexName() {
        return "accountBank";
    }
}
