package cn.zswltech.mithras.dto.liquiditymanage.dayReport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bigbear
 * @date 2024/12/13 16:41
 * @className DayReportIndicatorListRSP
 * @description
 */
@Data
@ApiModel(value = "日结指标列表响应参数")
public class DayReportIndicatorListRSP {

    @ApiModelProperty(value = "日期")
    private String date;

    @ApiModelProperty(value = "日初余额")
    private Long dawnOfDayBalance;

    @ApiModelProperty(value = "日初非监管户余额")
    private Long dawnOfDayNonSupervisionBalance;

    @ApiModelProperty(value = "日初非受限余额")
    private Long dawnOfDayNonRestrictedBalance;

    @ApiModelProperty(value = "今日计划租金流入")
    private Long todayPlanRentIncome;

    @ApiModelProperty(value = "今日计划还本付息")
    private Long todayPlanRepayPrincipalInterest;

    @ApiModelProperty(value = "日终余额")
    private Long endOfDayBalance;

    @ApiModelProperty(value = "日终非监管户余额")
    private Long endOfDayNonSupervisionBalance;

    @ApiModelProperty(value = "日终非受限余额")
    private Long endOfDayNonRestrictedBalance;
}
