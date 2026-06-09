package cn.zswltech.mithras.liquiditymanage.service.cal.account;

import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.liquiditymanage.enums.LiquidityIndexType;
import cn.zswltech.mithras.liquiditymanage.mapper.model.AccountBalanceBaseInfo;
import cn.zswltech.mithras.liquiditymanage.service.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.liquiditymanage.service.cal.bo.LiquidityAccountCalculatorBo;
import cn.zswltech.mithras.service.util.LongUtil;
import org.springframework.stereotype.Component;

/**
 * 差额
 *
 * 差异=结余（预估）-结余（实际）
 * @author chenyifei
 * @since 2024/12/22
 */
@Component
public class DiffAmountCalculator extends AbstractLiquidityCalculator<LiquidityAccountCalculatorBo> {
    @Override
    public void calculate(Object obj, LiquidityAccountCalculatorBo bo) {
        AccountBalanceBaseInfo accountBalanceBaseInfo = (AccountBalanceBaseInfo) obj;
        long result = LongUtil.null2zero(accountBalanceBaseInfo.getEstimateBalanceAmount()) - LongUtil.null2zero(accountBalanceBaseInfo.getActualBalanceAmount());
        ReflectUtil.setFieldValue(obj, indexName(), result);
    }

    @Override
    public LiquidityIndexType model() {
        return LiquidityIndexType.ACCOUNT_BALANCE;
    }

    @Override
    public String indexName() {
        return "diffAmount";
    }

    @Override
    public int sort() {
        return 40;
    }
}
