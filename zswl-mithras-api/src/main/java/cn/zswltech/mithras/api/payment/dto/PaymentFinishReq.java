package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/12/14
 * @description
 */
@Data
public class PaymentFinishReq {
    @ApiModelProperty(value = "付款申请id")
    @NotNull(message = "付款申请id不能为空")
    private Long paymentId;
}
