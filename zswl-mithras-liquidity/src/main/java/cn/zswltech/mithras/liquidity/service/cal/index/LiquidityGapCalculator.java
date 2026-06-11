package cn.zswltech.mithras.liquidity.service.cal.index;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.dto.liquiditymanage.base.LiquidityColorVo;
import cn.zswltech.mithras.basedata.enums.BaseDataBankAccountTypeEnum;
import cn.zswltech.mithras.liquidity.enums.LiquidityColorEnum;
import cn.zswltech.mithras.liquidity.enums.LiquidityIndexType;
import cn.zswltech.mithras.liquidity.mapper.model.AccountBalanceBaseInfo;
import cn.zswltech.mithras.liquidity.service.LiquidityIndicatorIndexHolder;
import cn.zswltech.mithras.liquidity.service.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.liquidity.service.cal.bo.LiquidityIndexCalculatorBo;
import cn.zswltech.mithras.foundation.util.LongUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;


/**
 * 流动性缺口(万元)
 *
 * =起止日时间范围内非监管户：（还本付息+还本付息（调整值））+刚性支出-租金流入
 * （范围限于非监管户）
 * 小于等于计算起始日的非监管户资金余额+灵活授信之和，标记为黑色
 * 大于计算起始日的非监管户资金余额+灵活授信之和，标记为红色
 * 备注：时间范围为指标模块左上角限定的起止时间
 *
 * @author chenyifei
 * @since 2024/12/18
 */
@Component
public class LiquidityGapCalculator extends AbstractLiquidityCalculator<LiquidityIndexCalculatorBo> {

    @Override
    public void calculate(Object obj, LiquidityIndexCalculatorBo bo) {
        // 起止日时间范围内非监管户：（还本付息+还本付息（调整值））+刚性支出-租金流入
        BigDecimal result = LiquidityIndicatorIndexHolder.ACCOUNT_BALANCE_BASE_INFO.entrySet().stream().filter(f -> {
            return !f.getKey().isBefore(bo.getQueryDateStart()) && !f.getKey().isAfter(bo.getQueryDateEnd());
        }).map(Map.Entry::getValue).map(Map::values).flatMap(Collection::stream).filter(f -> {
            return !Objects.equals(BaseDataBankAccountTypeEnum.SUPERVISION.name(), f.getAccountType());
        }).map(item -> {
            BigDecimal one = LongUtil.null2zeroBigDecimal(item.getRepayAmount()).add(LongUtil.null2zeroBigDecimal(item.getRepayEditAmount()));
            BigDecimal two = LongUtil.null2zeroBigDecimal(item.getMustExpenseAmount());
            BigDecimal three = LongUtil.null2zeroBigDecimal(item.getRentReflowAmount());
            return one.add(two).subtract(three);
        }).reduce(BigDecimal.ZERO, BigDecimal::add);

        // 计算起始日的非监管户资金余额+灵活授信之和
        Map<Long, AccountBalanceBaseInfo> accountBalanceBaseInfoMap = LiquidityIndicatorIndexHolder.ACCOUNT_BALANCE_BASE_INFO.get(bo.getQueryDateStart());
        BigDecimal two = BigDecimal.ZERO;
        if(CollectionUtil.isNotEmpty(accountBalanceBaseInfoMap)) {
            two = accountBalanceBaseInfoMap.values().stream().filter(f -> {
                        return !Objects.equals(BaseDataBankAccountTypeEnum.SUPERVISION.name(), f.getAccountType());
                    }).map(m -> m.getActualBalanceAmount() != null ? BigDecimal.valueOf(m.getActualBalanceAmount()) : LongUtil.null2zeroBigDecimal(m.getEstimateBalanceAmount()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .add(LongUtil.null2zeroBigDecimal(LiquidityIndicatorIndexHolder.PARAMETER_BASE_DETAIL.getFlexibleCredit()));
        }
        String color = result.compareTo(two) <= 0 ? LiquidityColorEnum.BLACK.name() : LiquidityColorEnum.RED.name();
        ReflectUtil.setFieldValue(obj, indexName(), new LiquidityColorVo(result, color));
    }

    @Override
    public LiquidityIndexType model() {
        return LiquidityIndexType.LIQUIDITY_INDEX;
    }

    @Override
    public String indexName() {
        return "liquidityGap";
    }

}
