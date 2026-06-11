package cn.zswltech.mithras.liquidity.service.cal.board;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.dto.liquiditymanage.base.LiquidityColorVo;
import cn.zswltech.mithras.liquidity.enums.LiquidityColorEnum;
import cn.zswltech.mithras.liquidity.enums.LiquidityIndexType;
import cn.zswltech.mithras.liquidity.model.AccountBalanceBaseInfo;
import cn.zswltech.mithras.liquidity.service.LiquidityIndicatorBoardHolder;
import cn.zswltech.mithras.liquidity.service.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.liquidity.service.cal.bo.LiquidityBoardCalculatorBo;
import cn.zswltech.mithras.foundation.util.LongUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;


/**
 * ABS还款(元)
 *
 * 账户明细表中当日全部账户的还本付息(abs）之和
 * @author chenyifei
 * @since 2024/12/20
 */
@Component
public class AbsRepaymentCalculator extends AbstractLiquidityCalculator<LiquidityBoardCalculatorBo> {

    @Override
    public void calculate(Object obj, LiquidityBoardCalculatorBo bo) {
        BigDecimal result = BigDecimal.ZERO;
        Map<Long, AccountBalanceBaseInfo> accountBalanceMap = LiquidityIndicatorBoardHolder.ACCOUNT_BALANCE_BASE_INFO.get(bo.getQueryDate());
        if(CollectionUtil.isNotEmpty(accountBalanceMap)){
            result = accountBalanceMap.values().stream().map(m -> {
                return LongUtil.null2zeroBigDecimal(m.getRepayAbsAmount());
            }).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        ReflectUtil.setFieldValue(obj, indexName(), new LiquidityColorVo(result, LiquidityColorEnum.BLACK.name(), 3));
    }

    @Override
    public LiquidityIndexType model() {
        return LiquidityIndexType.LIQUIDITY_BOARD;
    }

    @Override
    public String indexName() {
        return "absRepayment";
    }

}
