package cn.zswltech.mithras.dto.riskcontrol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description 客户集中度
 * @date 2023-02-27
 */
@Data
@ApiModel("客户集中度列表-返回体")
public class RiskControlConcentrationAllRelateRSP {
    @ApiModelProperty(value = "剩余本金")
    private Long remainingPrincipal;
    @ApiModelProperty(value = "集中度占比 %展示")
    private Long concentrationRatio;
    @ApiModelProperty(value = "不良余额")
    private Long badBalance;
    @ApiModelProperty(value = "不良余额占比")
    private Long badBalanceRatio;
}
