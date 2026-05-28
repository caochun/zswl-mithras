package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/6/23
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectPayNoSettleRSP extends DashboardProjectBasicRSP {
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("投放金额")
    private ValueUnitDTO actualPayAmount;
    @ApiModelProperty("存量风险敞口")
    private ValueUnitDTO stockRiskExposure;
    @ApiModelProperty("剩余金额")
    private ValueUnitDTO balanceAmount;
    @ApiModelProperty("剩余本金")
    private ValueUnitDTO principalBalanceAmount;
    @ApiModelProperty("剩余利息")
    private ValueUnitDTO interestBalanceAmount;
    @ApiModelProperty("保证金")
    private ValueUnitDTO earnestAmount;
    @ApiModelProperty("投放月份")
    private LocalDate actualPayMonth;
    @ApiModelProperty("实际起租日")
    private LocalDate actualLeaseDate;
    @ApiModelProperty("租赁期限")
    private ValueUnitDTO leaseDuration;
    @ApiModelProperty("剩余期限")
    private ValueUnitDTO remainingLeaseDuration;
    @ApiModelProperty("IRR")
    private ValueUnitDTO irr;
    @ApiModelProperty("地区分类code")
    private String regionalProjectClassifyCode;
    @ApiModelProperty("地区分类display")
    private String regionalProjectClassifyDisplay;
    @ApiModelProperty("是否关联方")
    private Integer isRelated;
}
