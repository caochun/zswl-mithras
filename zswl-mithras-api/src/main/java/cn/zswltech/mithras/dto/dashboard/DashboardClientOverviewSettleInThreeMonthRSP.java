package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/6/16
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardClientOverviewSettleInThreeMonthRSP extends DashboardClientBasicRSP {
    @ApiModelProperty("到期日")
    private LocalDate deadline;
    @ApiModelProperty("剩余期限")
    private Long remainingDuration;
    @ApiModelProperty("授信金额")
    private ValueUnitDTO creditAmount;
    @ApiModelProperty("剩余本金")
    private ValueUnitDTO principalBalanceAmount;
    @ApiModelProperty("存量风险敞口")
    private ValueUnitDTO stockRiskExposure;

    @ApiModelProperty("已收租金")
    private ValueUnitDTO collectionAmount;
    @ApiModelProperty("已收本金")
    private ValueUnitDTO collectionPrincipalAmount;
    @ApiModelProperty("已收利息")
    private ValueUnitDTO collectionInterestAmount;
   /* @ApiModelProperty("剩余本金")
    private ValueUnitDTO interestPrincipalAmount;*/
    @ApiModelProperty("剩余利息")
    private ValueUnitDTO interestInterestAmount;
}
