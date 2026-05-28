package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/12 16:24
 */
@ApiModel("创建付款申请-入参")
@Data
public class PaymentAddReq{
    @ApiModelProperty("合同id")
    private Long contractId;
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("客户名称")
    private Long clientId;
    @ApiModelProperty("客户名称")
    private String clientName;
    @ApiModelProperty("应付款项")
    private String payables;
    @ApiModelProperty("计划付款金额")
    private Long applyPaymentAmount;
    @ApiModelProperty("计划付款日期")
    private LocalDate applyPaymentDate;
    @ApiModelProperty("已付金额")
    private Long amountPaid;
    @ApiModelProperty("已申请金额")
    private Long amountApplied;
    @ApiModelProperty("剩余可申请金额")
    private Long remainingApplyAmount;
}
