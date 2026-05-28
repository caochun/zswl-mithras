package cn.zswltech.mithras.dto.dashboard.operation;

import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class DashboardOperationCapacityListRSP {

    @ApiModelProperty("部门id")
    private Long bizDeptId;

    @ApiModelProperty("部门名称")
    private String bizDeptName;

    @ApiModelProperty("调整人数")
    private ValueUnitDTO adjustPersonSum;

    @ApiModelProperty("总项目数")
    private ValueUnitDTO projSum;

    @ApiModelProperty("总金额/万元")
    private ValueUnitDTO amountSum;

    @ApiModelProperty("人均项目数")
    private ValueUnitDTO personAverageProjSum;

    @ApiModelProperty("人均金额/万元")
    private ValueUnitDTO personAverageAmountSum;

    @ApiModelProperty("件均金额/万元")
    private ValueUnitDTO pieceAverageAmountSum;

}
