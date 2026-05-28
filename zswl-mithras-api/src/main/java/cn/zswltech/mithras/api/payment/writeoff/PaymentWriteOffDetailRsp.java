package cn.zswltech.mithras.api.payment.writeoff;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/16 13:43
 */
@ApiModel("付款核销详情接口-出参")
@Data
public class PaymentWriteOffDetailRsp {
    @ApiModelProperty("申请编号")
    private String paymentCode;
    @ApiModelProperty("申请编号")
    private String writeOffStatus;
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("项目名称")
    private String projName;
    @ApiModelProperty("客户名称")
    private String clientName;
    @ApiModelProperty("业务类型")
    private String bizTypeCode;
    @ApiModelProperty("租赁类型")
    private String leaseTypeCode;
    @ApiModelProperty("业务部门")
    private String bizDeptName;
    @ApiModelProperty("项目主办")
    private String projSponsorUserName;
    @ApiModelProperty("现金流项目")
    private String payables;
    @ApiModelProperty("计划付款日期")
    private LocalDate planedPaidDate;
    @ApiModelProperty("计划付款金额")
    private Long planedPaidAmount;
    @ApiModelProperty("审批状态")
    private String processStatus;
    @ApiModelProperty("申请付款日期")
    private LocalDate applyPaymentDate;
    @ApiModelProperty("申请付款金额")
    private Long applyPaymentAmount;
    @ApiModelProperty("付款申请状态")
    private String paymentStatus;
    /**
     * 补充信息
     */
    @ApiModelProperty(value = "是否结束投放（用户选择）")
    private Boolean isFinishPut;
    @ApiModelProperty(value = "是否结束投放（最终结果）")
    private Boolean isFinishPutFinal;
    @ApiModelProperty(value = "租金表收款日")
    private Integer defaultCollectionDay;
}
