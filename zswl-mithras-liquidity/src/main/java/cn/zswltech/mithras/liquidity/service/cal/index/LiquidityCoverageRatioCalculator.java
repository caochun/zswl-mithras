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
import java.util.Optional;


/**
 * 流动性覆盖率
 *
 * 高流动性资产/高流动性负债
 * 大于100%时，标记为黑色
 * 小于等于100%，大于70%时，标记为黄色
 * 小于等于70%时，标记为红色
 *
 * @author chenyifei
 * @since 2024/12/18
 */
@Component
public class LiquidityCoverageRatioCalculator extends AbstractLiquidityCalculator<LiquidityIndexCalculatorBo> {

    @Override
    public void calculate(Object obj, LiquidityIndexCalculatorBo bo) {
        BigDecimal result = BigDecimal.ZERO;

        LiquidityIndexDetailRSP rsp = (LiquidityIndexDetailRSP)obj;
        LiquidityColorVo highLiquidityAssetsVo = rsp.getHighLiquidityAssets();
        LiquidityColorVo highLiquidityLiabilityVo = rsp.getHighLiquidityLiability();
        BigDecimal highLiquidityAsset = Optional.ofNullable(highLiquidityAssetsVo).map(LiquidityColorVo::getValue).orElse(BigDecimal.ZERO);
        BigDecimal highLiquidityLiability = Optional.ofNullable(highLiquidityLiabilityVo).map(LiquidityColorVo::getValue).orElse(BigDecimal.ZERO);
        if(highLiquidityLiability.compareTo(BigDecimal.ZERO) != 0) {
            result = highLiquidityAsset.divide(highLiquidityLiability, 10, RoundingMode.HALF_UP);
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
        return "liquidityCoverageRatio";
    }

    @Override
    public int sort() {
        return 20;
    }

}
