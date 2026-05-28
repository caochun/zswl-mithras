package cn.zswltech.mithras.dto.dashboard.operate;

import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class DashboardOperateTodoListRSP {
    @ApiModelProperty("授信金额")
    private ValueUnitDTO creditAmount;
    @ApiModelProperty("剩余本金")
    private ValueUnitDTO principalBalanceAmount;
    @ApiModelProperty("存量风险敞口")
    private ValueUnitDTO stockRiskExposure;

}
