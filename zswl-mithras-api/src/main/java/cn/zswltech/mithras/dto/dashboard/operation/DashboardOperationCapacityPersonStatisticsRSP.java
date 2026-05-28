package cn.zswltech.mithras.dto.dashboard.operation;

import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DashboardOperationCapacityPersonStatisticsRSP {
    @ApiModelProperty("部门id")
    private Long bizDeptId;

    @ApiModelProperty("部门名称")
    private String bizDeptName;

    @ApiModelProperty("实际投放金额/亿元")
    private ValueUnitDTO payAmount;

    @ApiModelProperty("预算投放金额/亿元")
    private ValueUnitDTO payPlanAmount;

    @ApiModelProperty("当期投放完成率/%")
    private ValueUnitDTO currentPayFinishRate;

    @ApiModelProperty("本年投放完成率/%")
    private ValueUnitDTO yearPayFinishRate;
}
