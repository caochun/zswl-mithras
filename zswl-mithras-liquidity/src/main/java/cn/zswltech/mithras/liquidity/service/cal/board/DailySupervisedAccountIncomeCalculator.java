package cn.zswltech.mithras.liquidity.service.cal.board;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.dto.liquiditymanage.base.LiquidityColorVo;
import cn.zswltech.mithras.liquidity.enums.LiquidityBankAccountType;
import cn.zswltech.mithras.liquidity.enums.LiquidityColorEnum;
import cn.zswltech.mithras.liquidity.enums.LiquidityIndexType;
import cn.zswltech.mithras.liquidity.model.AccountBalanceBaseInfo;
import cn.zswltech.mithras.liquidity.service.LiquidityIndicatorBoardHolder;
import cn.zswltech.mithras.liquidity.service.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.liquidity.service.cal.bo.LiquidityBoardCalculatorBo;
import cn.zswltech.mithras.foundation.util.LongUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * @author bigbear
 * @date 2025/2/20 17:11
 * @description
 */
@Slf4j
@Component
public class DailySupervisedAccountIncomeCalculator extends AbstractLiquidityCalculator<LiquidityBoardCalculatorBo> {
    @Override
    public void calculate(Object obj, LiquidityBoardCalculatorBo bo) {
        BigDecimal result = BigDecimal.ZERO;
        Map<Long, AccountBalanceBaseInfo> accountBalanceMap = LiquidityIndicatorBoardHolder.ACCOUNT_BALANCE_BASE_INFO.get(bo.getQueryDate());
        if (CollectionUtil.isNotEmpty(accountBalanceMap)) {
            // result = 提款+租金回流+其它流入-投放-(还本付息+还本付息(调整值))-刚性支出-其它支出
            result = accountBalanceMap.values().stream()
                    .filter(account -> Objects.equals(LiquidityBankAccountType.SUPERVISION.name(), account.getAccountType()))
                    .map(m -> LongUtil.null2zeroBigDecimal(m.getDrawingsAmount())
                            .add(LongUtil.null2zeroBigDecimal(m.getRentReflowAmount())
                                    .add(LongUtil.null2zeroBigDecimal(m.getOtherFlowAmount()))
                                    .subtract(LongUtil.null2zeroBigDecimal(m.getPaymentAmount()))
                                    .subtract(LongUtil.null2zeroBigDecimal(m.getRepayAmount()).add(LongUtil.null2zeroBigDecimal(m.getRepayEditAmount())))
                                    .subtract(LongUtil.null2zeroBigDecimal(m.getMustExpenseAmount()))
                                    .subtract(LongUtil.null2zeroBigDecimal(m.getOtherExpenseAmount())))).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        ReflectUtil.setFieldValue(obj, indexName(), new LiquidityColorVo(result, LiquidityColorEnum.BLACK.name(), 3));
    }

    @Override
    public LiquidityIndexType model() {
        return LiquidityIndexType.LIQUIDITY_BOARD;
    }

    @Override
    public String indexName() {
        return "dailySupervisedAccountIncome";
    }
}
