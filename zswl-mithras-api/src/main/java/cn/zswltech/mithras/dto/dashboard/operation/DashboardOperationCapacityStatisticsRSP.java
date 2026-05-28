package cn.zswltech.mithras.dto.dashboard.operation;

import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DashboardOperationCapacityStatisticsRSP {
    @ApiModelProperty("部门id")
    private Long bizDeptId;

    @ApiModelProperty("部门名称")
    private String bizDeptName;

    @ApiModelProperty("部门总项目数")
    private ValueUnitDTO projSum;

    @ApiModelProperty("部门总项目数/去年同期")
    private ValueUnitDTO lastProjSum;

    @ApiModelProperty("部门金额")
    private ValueUnitDTO amountSum;

    @ApiModelProperty("部门金额/去年同期")
    private ValueUnitDTO lastAmountSum;

    @ApiModelProperty("人均项目数")
    private ValueUnitDTO personAverageProjSum;

    @ApiModelProperty("人均项目数/去年同期")
    private ValueUnitDTO lastPersonAverageProjSum;

    @ApiModelProperty("人均金额")
    private ValueUnitDTO personAverageAmountSum;

    @ApiModelProperty("人均金额/去年同期")
    private ValueUnitDTO lastPersonAverageAmountSum;

    @ApiModelProperty("件均金额")
    private ValueUnitDTO pieceAverageAmountSum;

    @ApiModelProperty("件均金额/去年同期")
    private ValueUnitDTO lastPieceAverageAmountSum;


}
