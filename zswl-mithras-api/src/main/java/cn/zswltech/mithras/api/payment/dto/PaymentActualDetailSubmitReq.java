package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/12/11
 * @description
 */
@Data
public class PaymentActualDetailSubmitReq {
    @NotNull(message = "付款id不能为空")
    @ApiModelProperty("付款id")
    private Long paymentId;

    @NotNull(message = "是否结束投放不能为空")
    @ApiModelProperty("是否结束投放")
    private Boolean isFinishPut = false;

//    @NotNull(message = "是否需要运营提前审核不能为空")
//    @ApiModelProperty("是否需要运营提前审核")
//    private Boolean isLoanReviewAdvanced = false;
}
