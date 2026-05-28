package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/6/15
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectStageRepaymentDetailRSP extends DashboardProjectBasicRSP {
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("实际起租日")
    private LocalDate actualLeaseDate;
    @ApiModelProperty("实际投放日")
    private LocalDate actualPayDate;
    @ApiModelProperty("已收租金")
    private ValueUnitDTO totalCollectionRent;
    @ApiModelProperty("剩余金额")
    private ValueUnitDTO totalRentBalance;
    @ApiModelProperty("存量风险敞口")
    private ValueUnitDTO stockRiskExposure;
    @ApiModelProperty("投放金额")
    private ValueUnitDTO actualPayAmount;
    @ApiModelProperty("剩余本金")
    private ValueUnitDTO totalPrincipalBalance;
    @ApiModelProperty("剩余利息")
    private ValueUnitDTO totalInterestBalance;
    @ApiModelProperty("IRR")
    private ValueUnitDTO irr;
    @ApiModelProperty("租赁期限")
    private ValueUnitDTO leaseDuration;
    @ApiModelProperty("剩余期限")
    private ValueUnitDTO remainingLeaseDuration;
}
