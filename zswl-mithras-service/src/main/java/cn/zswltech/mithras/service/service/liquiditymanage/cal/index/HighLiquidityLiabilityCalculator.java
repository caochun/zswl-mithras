package cn.zswltech.mithras.service.service.liquiditymanage.cal.index;

import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.dto.liquiditymanage.base.LiquidityColorVo;
import cn.zswltech.mithras.service.enums.basedata.BaseDataBankAccountTypeEnum;
import cn.zswltech.mithras.service.enums.fund.liquidity.LiquidityColorEnum;
import cn.zswltech.mithras.service.enums.fund.liquidity.LiquidityIndexType;
import cn.zswltech.mithras.service.service.liquiditymanage.LiquidityIndicatorIndexHolder;
import cn.zswltech.mithras.service.service.liquiditymanage.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.service.service.liquiditymanage.cal.bo.LiquidityIndexCalculatorBo;
import cn.zswltech.mithras.service.util.LongUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;


/**
 * 高流动性负债(万元)
 *
 * =起止日时间范围内非监管户的（还本付息+还本付息（调整值））流出合计+起止日时间范围内非监管户的刚性支出流出合计
 * 备注：时间范围为指标模块左上角限定的起止时间
 *
 * @author chenyifei
 * @since 2024/12/18
 */
@Component
public class HighLiquidityLiabilityCalculator extends AbstractLiquidityCalculator<LiquidityIndexCalculatorBo> {

    @Override
    public void calculate(Object obj, LiquidityIndexCalculatorBo bo) {
        BigDecimal result = LiquidityIndicatorIndexHolder.ACCOUNT_BALANCE_BASE_INFO.entrySet().stream().filter(f -> {
            return !f.getKey().isBefore(bo.getQueryDateStart()) && !f.getKey().isAfter(bo.getQueryDateEnd());
        }).map(Map.Entry::getValue).map(Map::values).flatMap(Collection::stream).filter(f -> {
            return !Objects.equals(BaseDataBankAccountTypeEnum.SUPERVISION.name(), f.getAccountType());
        }).map(item -> {
            BigDecimal one = LongUtil.null2zeroBigDecimal(item.getRepayAmount()).add(LongUtil.null2zeroBigDecimal(item.getRepayEditAmount()));
            BigDecimal two = LongUtil.null2zeroBigDecimal(item.getMustExpenseAmount());
            return one.add(two);
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
        ReflectUtil.setFieldValue(obj, indexName(), new LiquidityColorVo(result, LiquidityColorEnum.BLACK.name()));
    }

    @Override
    public LiquidityIndexType model() {
        return LiquidityIndexType.LIQUIDITY_INDEX;
    }

    @Override
    public String indexName() {
        return "highLiquidityLiability";
    }

}
