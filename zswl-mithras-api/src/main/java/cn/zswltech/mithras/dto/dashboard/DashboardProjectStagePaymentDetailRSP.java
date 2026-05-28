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
public class DashboardProjectStagePaymentDetailRSP extends DashboardProjectBasicRSP {
    @ApiModelProperty("付款编号")
    private String paymentCode;
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("付款申请状态code")
    private String paymentStatusCode;
    @ApiModelProperty("付款申请状态display")
    private String paymentStatusDisplay;
    @ApiModelProperty("付款审批状态code")
    private String paymentProcessStatusCode;
    @ApiModelProperty("付款审批状态display")
    private String paymentProcessStatusDisplay;
    @ApiModelProperty("申请付款日期")
    private LocalDate applyPayDate;
    @ApiModelProperty("申请付款金额")
    private ValueUnitDTO applyPayAmount;
    @ApiModelProperty("合同金额")
    private ValueUnitDTO contractAmount;
    @ApiModelProperty("租赁利率")
    private ValueUnitDTO interestRate;
    @ApiModelProperty("租赁期限")
    private ValueUnitDTO leaseDuration;
}
