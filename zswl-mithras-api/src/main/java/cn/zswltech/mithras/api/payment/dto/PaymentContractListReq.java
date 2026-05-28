package cn.zswltech.mithras.api.payment.dto;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/12 16:06
 */
@ApiModel("付款合同查询-入参")
@Data
public class PaymentContractListReq {
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("客户id")
    @NotNull(message = "客户id不能为空")
    private Long clientId;
    @ApiModelProperty("计划付款金额范围-from")
    private Long planedPaidAmountFrom;
    @ApiModelProperty("计划付款金额范围-to")
    private Long planedPaidAmountTo;
    @ApiModelProperty("计划付款日期范围-from")
    private LocalDate planedPaidDateFrom;
    @ApiModelProperty("计划付款日期范围-to")
    private LocalDate planedPaidDateTo;
}
