package cn.zswltech.mithras.api.payment.dto;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/12 16:52
 */
@ApiModel("付款列表-入参")
@Data
public class PaymentListReq extends PageReq {
    @ApiModelProperty("客户id")
    private Long clientId;
    @ApiModelProperty("合同id")
    private Long contractId;
    @ApiModelProperty("合同code")
    private String contractCode;
    @ApiModelProperty("付款申请核销状态")
    private String writeOffStatus;
    @ApiModelProperty("付款申请审批状态")
    private String paymentProcessStatus;
    @ApiModelProperty("申请付款金额范围-from")
    private Long applyPaymentAmountFrom;
    @ApiModelProperty("申请付款金额范围-to")
    private Long applyPaymentAmountTo;
    @ApiModelProperty("申请付款日期范围-from")
    private LocalDate applyPaymentDateFrom;
    @ApiModelProperty("申请付款日期范围-to")
    private LocalDate applyPaymentDateTo;
    @ApiModelProperty("实付日期From")
    private LocalDate paidInDateFrom;
    @ApiModelProperty("实付日期To")
    private LocalDate paidInDateTo;
    @ApiModelProperty("记录状态")
    private String paymentStatus;
    @ApiModelProperty("记录状态")
    private List<String> paymentStatusList;
    @ApiModelProperty("排序字段-数据库字段名")
    private String orderFieldName;
    @ApiModelProperty("升序or降序：asc/ desc")
    private String order;
    @ApiModelProperty("业务部门id")
    private Long bizDeptId;
}
