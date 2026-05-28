package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/12 16:52
 */
@ApiModel("付款列表-出参")
@Data
public class PaymentListRsp {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("付款申请编号")
    private String paymentCode;
    @ApiModelProperty("合同id")
    private Long contractId;
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("客户id")
    private Long clientId;
    @ApiModelProperty("客户名称")
    private String clientName;
    @ApiModelProperty("申请付款日期")
    private LocalDate applyPaymentDate;
    @ApiModelProperty("申请付款金额")
    private Long applyPaymentAmount;
    @ApiModelProperty("申请状态")
    private String paymentStatus;
    @ApiModelProperty("审批状态")
    private String paymentProcessStatus;
    @ApiModelProperty("申请人")
    private String applicant;
    @ApiModelProperty("最新一笔核销日期，无核销不显示")
    private LocalDate paidInDate;
    @ApiModelProperty("核销状态")
    private String writeOffStatus;
    @ApiModelProperty("创建人id")
    private Long createBy;
    @ApiModelProperty("发起时间")
    private LocalDateTime createTime;
    private Long bizDeptId;
    @ApiModelProperty("超期天数")
    private Integer beyondDays;
}
