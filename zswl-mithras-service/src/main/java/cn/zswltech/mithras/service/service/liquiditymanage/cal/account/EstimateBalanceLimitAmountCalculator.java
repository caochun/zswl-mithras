package cn.zswltech.mithras.service.service.liquiditymanage.cal.account;

import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.service.enums.basedata.BaseDataBankAccountTypeEnum;
import cn.zswltech.mithras.service.enums.fund.liquidity.LiquidityIndexType;
import cn.zswltech.mithras.service.mapper.model.basedata.BaseDataBankAccount;
import cn.zswltech.mithras.service.mapper.model.liquiditymanage.AccountBalanceBaseInfo;
import cn.zswltech.mithras.service.service.liquiditymanage.LiquidityIndicatorHolder;
import cn.zswltech.mithras.service.service.liquiditymanage.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.service.service.liquiditymanage.cal.bo.LiquidityAccountManualCalculatorBo;
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
//        BaseDataBankAccount baseDataBankAccount = LiquidityIndicatorHolder.BASE_DATA_BANK_ACCOUNT.get(bo.getAccountBankId());
        Long result = 0L;
//        switch (BaseDataBankAccountTypeEnum.find(baseDataBankAccount.getAccountType())){
//            case SUPERVISION:
//                AccountBalanceBaseInfo accountBalance = (AccountBalanceBaseInfo) obj;
//                if(accountBalance != null) {
//                    result = Optional.ofNullable(Optional.ofNullable(accountBalance.getEstimateBalanceLimitEditAmount()).orElse(accountBalance.getEstimateBalanceAmount())).orElse(0L);
//                }
//                break;
//            default:
                AccountBalanceBaseInfo accountBalanceBeforeOneDay = bo.getAccountBalanceBeforeDay();
                if(accountBalanceBeforeOneDay != null) {
                    result = Optional.ofNullable(Optional.ofNullable(accountBalanceBeforeOneDay.getEstimateBalanceLimitEditAmount()).orElse(accountBalanceBeforeOneDay.getEstimateBalanceLimitAmount())).orElse(0L);
                }
//        }

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
