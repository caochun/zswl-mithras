package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author bigbear
 * @date 2024/12/23 10:43
 * @className PaymentCheckApplyAmountReq
 * @description
 */
@Data
public class PaymentCheckApplyAmountReq {

    @ApiModelProperty(value = "付款申请ID")
    @NotNull(message = "付款申请ID不能为空")
    private Long paymentId;

    @ApiModelProperty("节点id")
    private String activityId;

}
