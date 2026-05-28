package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
@ApiModel("比对承租人及担保人工商信息")
public class PaymentClientCompareReq {
    @ApiModelProperty("付款id")
    @NotNull(message = "付款id不能为空")
    private Long paymentId;

    private String flowId;
}
