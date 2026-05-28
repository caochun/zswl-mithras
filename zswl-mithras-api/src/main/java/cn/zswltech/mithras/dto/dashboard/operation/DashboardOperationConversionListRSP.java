package cn.zswltech.mithras.dto.dashboard.operation;

import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DashboardOperationConversionListRSP {

    @ApiModelProperty("部门Id")
    private Long bizDeptId;

    @ApiModelProperty("部门名称")
    private String bizDeptName;

    @ApiModelProperty("调整人数")
    private ValueUnitDTO adjustCount;

    @ApiModelProperty("拜访个数")
    private ValueUnitDTO visitCount;

    @ApiModelProperty("立项个数")
    private ValueUnitDTO projEstaCount;

    @ApiModelProperty("尽调个数")
    private ValueUnitDTO dueDiligenceCount;

    @ApiModelProperty("评审个数")
    private ValueUnitDTO reviewCount;

    @ApiModelProperty("投放个数")
    private ValueUnitDTO deliveryCount;

    @ApiModelProperty("「访客-立项」转化率-立项数/访客数：当期")
    private ValueUnitDTO projEstaVisitCurrentTerm;

    @ApiModelProperty("「访客-立项」转化率-立项数/访客数：去年同期")
    private ValueUnitDTO projEstaVisitLastTerm;

    @ApiModelProperty("「访客-立项」转化率-立项数/访客数：当年平均")
    private ValueUnitDTO projEstaVisitCurrentAverage;

    @ApiModelProperty("「访客-立项」转化率-立项数/访客数：去年平均")
    private ValueUnitDTO projEstaVisitLastAverage;

    @ApiModelProperty("「访客-投放」转化率-投放个数/访客个数：当期")
    private ValueUnitDTO deliveryVisitCurrentTerm;

    @ApiModelProperty("「访客-投放」转化率-投放个数/访客个数：去年同期")
    private ValueUnitDTO deliveryVisitLastTerm;

    @ApiModelProperty("「访客-投放」转化率-投放个数/访客个数：去年平均")
    private ValueUnitDTO deliveryVisitLastAverage;

    @ApiModelProperty("「访客-投放」转化率-投放个数/访客个数：当年平均")
    private ValueUnitDTO deliveryVisitCurrentAverage;

    @ApiModelProperty("「立项-尽调」转化率-尽调数/立项数：当期")
    private ValueUnitDTO dueDiliProjEstaCurrentTerm;

    @ApiModelProperty("「立项-尽调」转化率-尽调数/立项数：去年同期")
    private ValueUnitDTO dueDiliProjEstaLastTerm;

    @ApiModelProperty("「立项-尽调」转化率-尽调数/立项数：当年平均")
    private ValueUnitDTO dueDiliProjEstaCurrentAverage;

    @ApiModelProperty("「立项-尽调」转化率-尽调数/立项数：去年平均")
    private ValueUnitDTO dueDiliProjEstaLastAverage;

    @ApiModelProperty("「尽调-评审」转化率-评审数/尽调数：当期")
    private ValueUnitDTO reviewDueDiliCurrentTerm;

    @ApiModelProperty("「尽调-评审」转化率-评审数/尽调数：去年同期")
    private ValueUnitDTO reviewDueDiliLastTerm;

    @ApiModelProperty("「尽调-评审」转化率-评审数/尽调数：去年平均")
    private ValueUnitDTO reviewDueDiliLastAverage;

    @ApiModelProperty("「尽调-评审」转化率-评审数/尽调数：当年平均")
    private ValueUnitDTO reviewDueDiliCurrentAverage;

    @ApiModelProperty("「尽调-投放」转化率-投放数/尽调数：当期")
    private ValueUnitDTO deliveryDueDiliCurrentTerm;

    @ApiModelProperty("「尽调-投放」转化率-投放数/尽调数：去年同期")
    private ValueUnitDTO deliveryDueDiliLastTerm;

    @ApiModelProperty("「尽调-投放」转化率-投放数/尽调数：去年平均")
    private ValueUnitDTO deliveryDueDiliLastAverage;

    @ApiModelProperty("「尽调-投放」转化率-投放数/尽调数：当年平均")
    private ValueUnitDTO deliveryDueDiliCurrentAverage;

    @ApiModelProperty("「评审-投放」转化率-投放数/评审数：当期")
    private ValueUnitDTO deliveryReviewCurrentTerm;

    @ApiModelProperty("「评审-投放」转化率-投放数/评审数：去年同期")
    private ValueUnitDTO deliveryReviewLastTerm;

    @ApiModelProperty("「评审-投放」转化率-投放数/评审数：去年平均")
    private ValueUnitDTO deliveryReviewLastAverage;

    @ApiModelProperty("「评审-投放」转化率-投放数/评审数：当年平均")
    private ValueUnitDTO deliveryReviewCurrentAverage;
}
