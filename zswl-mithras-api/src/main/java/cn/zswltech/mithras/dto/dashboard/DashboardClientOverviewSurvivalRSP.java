package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2024/6/16
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardClientOverviewSurvivalRSP extends DashboardClientBasicRSP {
    @ApiModelProperty("授信金额")
    private ValueUnitDTO creditAmount;
    @ApiModelProperty("剩余本金")
    private ValueUnitDTO principalBalanceAmount;
    @ApiModelProperty("存量风险敞口")
    private ValueUnitDTO stockRiskExposure;
}
