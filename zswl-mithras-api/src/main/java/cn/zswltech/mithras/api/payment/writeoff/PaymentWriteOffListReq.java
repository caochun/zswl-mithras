package cn.zswltech.mithras.api.payment.writeoff;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/16 12:00
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel("付款核销主页查询接口-入参")
@Data
public class PaymentWriteOffListReq extends PageReq {

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("核销状态")
    private String writeOffStatus;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("申请金额from")
    private Long applyPaymentAmountFrom;
    @ApiModelProperty("申请金额to")
    private Long applyPaymentAmountTo;

    @ApiModelProperty("申请日期from")
    private LocalDate applyPaymentDateFrom;
    @ApiModelProperty("申请日期to")
    private LocalDate applyPaymentDateTo;

    @ApiModelProperty("实付日期From")
    private LocalDate paidInDateFrom;
    @ApiModelProperty("实付日期To")
    private LocalDate paidInDateTo;

    @ApiModelProperty("业务部门id")
    private Long bizDeptId;
}
