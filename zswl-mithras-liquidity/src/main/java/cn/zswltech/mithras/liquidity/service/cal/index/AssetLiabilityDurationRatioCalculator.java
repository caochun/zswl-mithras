package cn.zswltech.mithras.liquidity.service.cal.index;

import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.dto.liquiditymanage.base.LiquidityColorVo;
import cn.zswltech.mithras.dto.liquiditymanage.base.ParameterIndexDetailRSP;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityindex.LiquidityIndexDetailRSP;
import cn.zswltech.mithras.liquidity.enums.FundParameterSignType;
import cn.zswltech.mithras.liquidity.enums.LiquidityColorEnum;
import cn.zswltech.mithras.liquidity.enums.LiquidityIndexType;
import cn.zswltech.mithras.liquidity.service.LiquidityIndicatorIndexHolder;
import cn.zswltech.mithras.liquidity.service.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.liquidity.service.cal.bo.LiquidityIndexCalculatorBo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


/**
 * 资产负债久期比
 *
 * 负债久期/资产久期
 * 大于等于0.69时，标记为红色
 * 小于0.69，大于等于0.65时，标记为黄色
 * 小于0.65时，标记为黑色
 *
 * @author chenyifei
 * @since 2024/12/18
 */
@Component
public class AssetLiabilityDurationRatioCalculator extends AbstractLiquidityCalculator<LiquidityIndexCalculatorBo> {

    @Override
    public void calculate(Object obj, LiquidityIndexCalculatorBo bo) {
        BigDecimal result = BigDecimal.ZERO;

        LiquidityIndexDetailRSP rsp = (LiquidityIndexDetailRSP)obj;
        LiquidityColorVo durationLiabilityVo = rsp.getDurationLiability();
        LiquidityColorVo durationAssetsVo = rsp.getDurationAssets();
        BigDecimal durationLiability = Optional.ofNullable(durationLiabilityVo).map(LiquidityColorVo::getValue).orElse(BigDecimal.ZERO);
        BigDecimal durationAssets = Optional.ofNullable(durationAssetsVo).map(LiquidityColorVo::getValue).orElse(BigDecimal.ZERO);
        if(durationAssets.compareTo(BigDecimal.ZERO) != 0) {
            result = durationLiability.divide(durationAssets, 10, RoundingMode.HALF_UP);
        }
        String color = LiquidityColorEnum.BLACK.name();
        ParameterIndexDetailRSP indexDetail = LiquidityIndicatorIndexHolder.PARAMETER_INDEX_DETAIL.get(indexName());
        if(indexDetail != null){
            color = FundParameterSignType.getColor(result ,indexDetail);
        }
        ReflectUtil.setFieldValue(obj, indexName(), new LiquidityColorVo(result, color));
    }

    @Override
    public LiquidityIndexType model() {
        return LiquidityIndexType.LIQUIDITY_INDEX;
    }

    @Override
    public String indexName() {
        return "assetLiabilityDurationRatio";
    }

    @Override
    public int sort() {
        return 20;
    }

}
