package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/15 11:15
 */
@ApiModel("问卷调查-入参")
@Data
public class PaymentQuestionListReq {
    @ApiModelProperty("付款id")
    @NotNull(message = "paymentId 为空")
    private Long paymentId;
}
