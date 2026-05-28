package cn.zswltech.mithras.api.payment.writeoff;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/16 11:54
 */
@ApiModel("付款核销主页查询接口-出参")
@Data
public class PaymentWriteOffListRsp {
    @ApiModelProperty("付款id")
    private Long paymentId;
    @ApiModelProperty("付款申请编号")
    private String paymentCode;
    @ApiModelProperty("合同id")
    private Long contractId;
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("项目类型")
    private String projectBizType;
    @ApiModelProperty("租赁类型")
    private String leaseType;
    @ApiModelProperty("客户id")
    private Long clientId;
    @ApiModelProperty("客户name")
    private String clientName;
    @ApiModelProperty("申请金额")
    private Long applyPaymentAmount;
    @ApiModelProperty("申请日期")
    private LocalDate applyPaymentDate;
    @ApiModelProperty("最新一笔核销日期，无核销不显示")
    private LocalDate paidInDate;
    @ApiModelProperty("核销状态")
    private String writeOffStatus;
    @ApiModelProperty("已付金额")
    private Long paidAmount;
    @ApiModelProperty("剩余金额=应付减已付")
    private Long remainingAmount;
    @ApiModelProperty("付款申请状态")
    private String paymentStatus;
    private Long bizDeptId;
    private String bizDeptName;
    @ApiModelProperty("超期天数")
    private Integer beyondDays;

}
