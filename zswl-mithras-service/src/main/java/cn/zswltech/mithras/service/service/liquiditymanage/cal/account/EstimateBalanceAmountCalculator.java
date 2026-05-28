package cn.zswltech.mithras.service.service.liquiditymanage.cal.account;

import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.service.enums.fund.liquidity.LiquidityIndexType;
import cn.zswltech.mithras.service.mapper.model.liquiditymanage.AccountBalanceBaseInfo;
import cn.zswltech.mithras.service.service.liquiditymanage.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.service.service.liquiditymanage.cal.bo.LiquidityAccountManualCalculatorBo;
import cn.zswltech.mithras.service.util.LongUtil;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 *  结余（预估）
 *
 *  上一日提款结余（实际）+提款+租金回流+其它流入-投放-（还本付息+还本付息（调整值））-刚性支出-其它支出
 *  备注：如上一日结余（实际）为空，则取上一日结余（预估）
 *  @author chenyifei
 *  @since 2024/12/16
 */
@Component
public class EstimateBalanceAmountCalculator extends AbstractLiquidityCalculator<LiquidityAccountManualCalculatorBo> {

    @Override
    public void calculate(Object obj, LiquidityAccountManualCalculatorBo bo) {
        Long beforeDayBalanceAmount = Optional.ofNullable(bo.getAccountBalanceBeforeDay())
                .map(m -> m.getActualBalanceAmount() != null ? m.getActualBalanceAmount() : m.getEstimateBalanceAmount()).orElse(0L);
        AccountBalanceBaseInfo accountBalance = (AccountBalanceBaseInfo) obj;
        if(accountBalance != null){
            ReflectUtil.setFieldValue(obj, indexName(),
                    beforeDayBalanceAmount +
                            LongUtil.null2zero(accountBalance.getDrawingsAmount()) +
                            LongUtil.null2zero(accountBalance.getRentReflowAmount()) +
                            LongUtil.null2zero(accountBalance.getOtherFlowAmount()) -
                            LongUtil.null2zero(accountBalance.getPaymentAmount()) -
                            LongUtil.null2zero(accountBalance.getRepayAmount()) -
                            LongUtil.null2zero(accountBalance.getRepayEditAmount()) -
                            LongUtil.null2zero(accountBalance.getMustExpenseAmount()) -
                            LongUtil.null2zero(accountBalance.getOtherExpenseAmount())
                    );
        }

    }

    @Override
    public LiquidityIndexType model() {
        return LiquidityIndexType.MANUAL_TRIGGER;
    }

    @Override
    public String indexName() {
        return "estimateBalanceAmount";
    }
}
