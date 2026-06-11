package cn.zswltech.mithras.liquidity.service.cal.index;

import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.dto.liquiditymanage.base.LiquidityColorVo;
import cn.zswltech.mithras.dto.liquiditymanage.base.ParameterIndexDetailRSP;
import cn.zswltech.mithras.liquidity.enums.FundParameterSignType;
import cn.zswltech.mithras.liquidity.enums.LiquidityColorEnum;
import cn.zswltech.mithras.liquidity.enums.LiquidityIndexType;
import cn.zswltech.mithras.credit.creditlimit.service.bo.CreditLimitDetailBO;
import cn.zswltech.mithras.liquidity.service.LiquidityIndicatorIndexHolder;
import cn.zswltech.mithras.liquidity.service.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.liquidity.service.cal.bo.LiquidityIndexCalculatorBo;
import cn.zswltech.mithras.foundation.util.LongUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;


/**
 * 可用授信比（%）
 *
 * 数据范围：授信状态=生效
 * 可用授信额度/总授信额度
 * 大于等于35%，标记为黑色
 * 小于35%，标记为红色
 * @author chenyifei
 * @since 2024/12/18
 */
@Component
public class AvailableCreditRatioCalculator extends AbstractLiquidityCalculator<LiquidityIndexCalculatorBo> {

    @Override
    public void calculate(Object obj, LiquidityIndexCalculatorBo bo) {
        BigDecimal result = BigDecimal.ZERO;
        Collection<CreditLimitDetailBO> creditDetailList = LiquidityIndicatorIndexHolder.CREDIT_LIMIT_DETAIL.values();
        BigDecimal totalLimit = creditDetailList.stream().map(m -> LongUtil.null2zeroBigDecimal(m.getTotalLimit())).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal occupyLimit = creditDetailList.stream().map(m -> LongUtil.null2zeroBigDecimal(m.getOccupyTotalLimit())).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal remainingLimit = totalLimit.subtract(occupyLimit);
        if (totalLimit.compareTo(BigDecimal.ZERO) != 0) {
            result = remainingLimit.divide(totalLimit, 10 , RoundingMode.HALF_UP);
        }
        ParameterIndexDetailRSP indexDetail = LiquidityIndicatorIndexHolder.PARAMETER_INDEX_DETAIL.get(indexName());
        String color = LiquidityColorEnum.BLACK.name();
        if(indexDetail != null) {
            color = FundParameterSignType.getColor(result , indexDetail);
        }
        ReflectUtil.setFieldValue(obj, indexName(), new LiquidityColorVo(result, color));
    }

    @Override
    public LiquidityIndexType model() {
        return LiquidityIndexType.LIQUIDITY_INDEX;
    }

    @Override
    public String indexName() {
        return "availableCreditRatio";
    }

}
