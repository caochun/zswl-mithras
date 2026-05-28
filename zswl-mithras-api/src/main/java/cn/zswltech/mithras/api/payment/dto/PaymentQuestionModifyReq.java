package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/15 11:21
 */
@ApiModel("问卷更新-入参")
@Data
public class PaymentQuestionModifyReq {
    @ApiModelProperty("答案id")
    private Long id;
    @ApiModelProperty("付款id")
    @NotNull(message = "paymentId 为 null")
    private Long paymentId;
    @ApiModelProperty("问题id")
    @NotNull(message = "questionId 为 null")
    private Long questionId;
    @ApiModelProperty("是否满足")
    @NotNull(message = "questionAnswer 为 null")
    private String questionAnswer;
    @ApiModelProperty("备注")
    private String remarks;
}
