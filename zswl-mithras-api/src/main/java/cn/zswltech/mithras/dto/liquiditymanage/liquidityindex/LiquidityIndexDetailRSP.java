package cn.zswltech.mithras.dto.liquiditymanage.liquidityindex;

import cn.zswltech.mithras.dto.liquiditymanage.base.LiquidityColorVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * LiquidityIndexDetailRSP
 *
 * @author chenyifei
 * @since 2024/12/16
 */
@Data
@ApiModel(value = "流动性指标返回体")
public class LiquidityIndexDetailRSP {

    @ApiModelProperty(value = "负债久期")
    private LiquidityColorVo durationLiability;

    @ApiModelProperty(value = "资产久期")
    private LiquidityColorVo durationAssets;

    @ApiModelProperty(value = "资产久期（质押/监管）")
    private LiquidityColorVo durationAssetsPledgedSupervised;

    @ApiModelProperty(value = "资产负债久期比")
    private LiquidityColorVo assetLiabilityDurationRatio;

    @ApiModelProperty(value = "高流动性资产")
    private LiquidityColorVo highLiquidityAssets;

    @ApiModelProperty(value = "高流动性负债")
    private LiquidityColorVo highLiquidityLiability;

    @ApiModelProperty(value = "流动性覆盖率")
    private LiquidityColorVo liquidityCoverageRatio;

    @ApiModelProperty(value = "流动性缺口")
    private LiquidityColorVo liquidityGap;

    @ApiModelProperty(value = "流动性缺口率")
    private LiquidityColorVo liquidityGapRate;

    @ApiModelProperty(value = "可用授信比")
    private LiquidityColorVo availableCreditRatio;
}
