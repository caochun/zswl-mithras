package cn.zswltech.mithras.dto.dashboard.operation;

import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DashboardOperationPayListRSP {

    @ApiModelProperty("部门Id")
    private Long bizDeptId;

    @ApiModelProperty("部门名称")
    private String bizDeptName;

    @ApiModelProperty("实际投放金额/万元")
    private ValueUnitDTO payAmount;

    @ApiModelProperty("预算投放金额/万元")
    private ValueUnitDTO payPlanAmount;

    @ApiModelProperty("差额/万元")
    private ValueUnitDTO difference;

    @ApiModelProperty("达成率/%")
    private ValueUnitDTO finishRate;

}
