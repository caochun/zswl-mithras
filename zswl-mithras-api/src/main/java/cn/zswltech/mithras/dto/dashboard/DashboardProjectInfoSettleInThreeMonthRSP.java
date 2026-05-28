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
public class DashboardProjectInfoSettleInThreeMonthRSP extends DashboardProjectBasicRSP {
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("到期日")
    private LocalDate deadline;
    @ApiModelProperty("剩余期限")
    private ValueUnitDTO remainingDuration;
    @ApiModelProperty("已收金额")
    private ValueUnitDTO collectionAmount;
    @ApiModelProperty("剩余金额")
    private ValueUnitDTO remainingAmount;
    @ApiModelProperty("保证金金额")
    private ValueUnitDTO earnestBalanceAmount;
    @ApiModelProperty("已收本金")
    private ValueUnitDTO collectionPrincipalAmount;
    @ApiModelProperty("已收利息")
    private ValueUnitDTO collectionInterestAmount;
    @ApiModelProperty("剩余本金")
    private ValueUnitDTO principalBalanceAmount;
    @ApiModelProperty("剩余利息")
    private ValueUnitDTO interestBalanceAmount;
    @ApiModelProperty("最后一期租金还款日")
    private LocalDate lastRentPlanCollectionDate;
}
