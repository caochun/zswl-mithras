package cn.zswltech.mithras.dto.dashboard.boss;

import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangxiong
 * @date 2024/5/15/17:58
 * @description
 */
@Data
public class AssetsFiveClassifyListRSP {

    @ApiModelProperty(value = "五级分类code")
    private String assetClassifyResultCode;

    @ApiModelProperty(value = "五级分类display")
    private String assetClassifyResultDisplay;

    @ApiModelProperty(value = "所属当前分类数量")
    private Integer quantity;

    @ApiModelProperty(value = "风险敞口")
    private ValueUnitDTO riskExposure;

    @ApiModelProperty(value = "资产余额")
    private ValueUnitDTO assetBalance;

    @ApiModelProperty(value = "占比")
    private ValueUnitDTO proportion;

    @ApiModelProperty(value = "环比")
    private ValueUnitDTO chainRatio;
}
