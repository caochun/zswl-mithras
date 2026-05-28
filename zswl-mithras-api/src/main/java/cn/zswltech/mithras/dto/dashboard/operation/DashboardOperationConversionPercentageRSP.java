package cn.zswltech.mithras.dto.dashboard.operation;

import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DashboardOperationConversionPercentageRSP {

    @ApiModelProperty("时间段")
    private String termName;

    @ApiModelProperty("访客-立项")
    private ValueUnitDTO visitProjEstaPer;

    @ApiModelProperty("访客-投放")
    private ValueUnitDTO visitDeliveryPer;

    @ApiModelProperty("立项-尽调")
    private ValueUnitDTO projEstaDueDiliPer;

    @ApiModelProperty("尽调-评审")
    private ValueUnitDTO dueDiligenceReviewPer;

    @ApiModelProperty("尽调-投放")
    private ValueUnitDTO dueDiliDeliveryPer;

    @ApiModelProperty("评审-投放")
    private ValueUnitDTO reviewDeliveryPer;

}
