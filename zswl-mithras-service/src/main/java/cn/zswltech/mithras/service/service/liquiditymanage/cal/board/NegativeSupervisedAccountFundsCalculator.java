package cn.zswltech.mithras.service.service.liquiditymanage.cal.board;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.dto.liquiditymanage.base.LiquidityColorVo;
import cn.zswltech.mithras.service.enums.basedata.BaseDataBankAccountTypeEnum;
import cn.zswltech.mithras.service.enums.fund.liquidity.LiquidityColorEnum;
import cn.zswltech.mithras.service.enums.fund.liquidity.LiquidityIndexType;
import cn.zswltech.mithras.service.mapper.model.liquiditymanage.AccountBalanceBaseInfo;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.liquiditymanage.LiquidityIndicatorBoardHolder;
import cn.zswltech.mithras.service.service.liquiditymanage.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.service.service.liquiditymanage.cal.bo.LiquidityBoardCalculatorBo;
import cn.zswltech.mithras.service.util.LongUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


/**
 * 监管户资金的负值(元)
 *
 * 账户明细表中当日账户性质=监管户，且结余（实际）为负值的结余（实际）之和
 * 无结余（实际）则取结余（预估）
 * @author chenyifei
 * @since 2024/12/20
 */
@Component
public class NegativeSupervisedAccountFundsCalculator extends AbstractLiquidityCalculator<LiquidityBoardCalculatorBo> {

    @Override
    public void calculate(Object obj, LiquidityBoardCalculatorBo bo) {
        BigDecimal result = BigDecimal.ZERO;
        Map<Long, AccountBalanceBaseInfo> accountBalanceMap = LiquidityIndicatorBoardHolder.ACCOUNT_BALANCE_BASE_INFO.get(bo.getQueryDate());
        if(CollectionUtil.isNotEmpty(accountBalanceMap)){
            result = accountBalanceMap.values().stream().filter(f -> {
                return Objects.equals(f.getAccountType(), BaseDataBankAccountTypeEnum.SUPERVISION.name());
            }).map(m -> {
                long value = Optional.ofNullable(m.getActualBalanceAmount()).orElse(LongUtil.null2zero(m.getEstimateBalanceAmount()));
                return value < 0 ? BigDecimal.valueOf(value) : BigDecimal.ZERO;
            }).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        ReflectUtil.setFieldValue(obj, indexName(), new LiquidityColorVo(result, LiquidityColorEnum.BLACK.name(), 2));
    }

    @Override
    public LiquidityIndexType model() {
        return LiquidityIndexType.LIQUIDITY_BOARD;
    }

    @Override
    public String indexName() {
        return "negativeSupervisedAccountFunds";
    }

}
