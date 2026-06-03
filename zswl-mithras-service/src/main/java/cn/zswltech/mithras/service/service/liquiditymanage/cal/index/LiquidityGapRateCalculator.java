package cn.zswltech.mithras.service.service.liquiditymanage.cal.index;

import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.dto.liquiditymanage.base.LiquidityColorVo;
import cn.zswltech.mithras.dto.liquiditymanage.base.ParameterIndexDetailRSP;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityIndex.LiquidityIndexDetailRSP;
import cn.zswltech.mithras.basedata.enums.BaseDataBankAccountTypeEnum;
import cn.zswltech.mithras.liquiditymanage.enums.FundParameterSignType;
import cn.zswltech.mithras.liquiditymanage.enums.LiquidityColorEnum;
import cn.zswltech.mithras.liquiditymanage.enums.LiquidityIndexType;
import cn.zswltech.mithras.service.service.liquiditymanage.LiquidityIndicatorIndexHolder;
import cn.zswltech.mithras.service.service.liquiditymanage.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.liquiditymanage.service.cal.bo.LiquidityIndexCalculatorBo;
import cn.zswltech.mithras.service.util.LongUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


/**
 * 流动性缺口率
 *
 * 流动性缺口/起止日时间范围内非监管户的租金流入合计
 * 大于等于10%，标记为红色
 * 小于10%，标记为黑色
 * 备注：时间范围为指标模块左上角限定的起止时间
 *
 * @author chenyifei
 * @since 2024/12/18
 */
@Component
public class LiquidityGapRateCalculator extends AbstractLiquidityCalculator<LiquidityIndexCalculatorBo> {

    @Override
    public void calculate(Object obj, LiquidityIndexCalculatorBo bo) {
        BigDecimal result = BigDecimal.ZERO;
        // 流动性缺口
        LiquidityIndexDetailRSP detail = (LiquidityIndexDetailRSP) obj;
        BigDecimal one = Optional.ofNullable(detail.getLiquidityGap()).map(LiquidityColorVo::getValue).orElse(BigDecimal.ZERO);

        // 起止日时间范围内非监管户的租金流入合计
        long two = LiquidityIndicatorIndexHolder.ACCOUNT_BALANCE_BASE_INFO.entrySet().stream().filter(f -> {
            return !f.getKey().isBefore(bo.getQueryDateStart()) && !f.getKey().isAfter(bo.getQueryDateEnd());
        }).map(Map.Entry::getValue).map(Map::values).flatMap(Collection::stream).filter(f -> {
            return !Objects.equals(BaseDataBankAccountTypeEnum.SUPERVISION.name(), f.getAccountType());
        }).mapToLong(item -> LongUtil.null2zero(item.getRentReflowAmount())).sum();
        if(two != 0) {
            result = one.divide(BigDecimal.valueOf(two), 10, RoundingMode.HALF_UP);
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
        return "liquidityGapRate";
    }

    @Override
    public int sort() {
        return 20;
    }
}
