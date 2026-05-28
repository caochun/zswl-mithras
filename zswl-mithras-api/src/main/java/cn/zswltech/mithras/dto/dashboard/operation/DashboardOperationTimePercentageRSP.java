package cn.zswltech.mithras.dto.dashboard.operation;

import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DashboardOperationTimePercentageRSP {

    @ApiModelProperty("时间段")
    private String termName;

    @ApiModelProperty("立项均耗")
    private ValueUnitDTO projEstaTotalTime;

    @ApiModelProperty("尽调-出具尽调报告均耗")
    private ValueUnitDTO dueDiligenceTotalTime;

    @ApiModelProperty("评审均耗")
    private ValueUnitDTO reviewTotalTime;

    @ApiModelProperty("纪要均耗")
    private ValueUnitDTO summaryTotalTime;

    @ApiModelProperty("立项-投放均耗")
    private ValueUnitDTO projEstaPaidInTotalTime;

    @ApiModelProperty("评审-投放均耗")
    private ValueUnitDTO reviewPaidInTotalTime;

}
