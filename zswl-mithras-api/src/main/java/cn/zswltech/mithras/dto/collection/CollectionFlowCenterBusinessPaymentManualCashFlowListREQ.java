package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author bigbear
 * @date 2024/10/14 10:35
 * @description
 */
@Data
public class CollectionFlowCenterBusinessPaymentManualCashFlowListREQ {

    @ApiModelProperty(value = "付款申请ID")
    @NotNull(message = "付款申请ID不能为空")
    private Long paymentId;

    @ApiModelProperty(value = "付款计划ID")
    private Long paymentActualDetailId;

    @ApiModelProperty(value = "付款方式名称")
    @NotBlank(message = "付款方式名称不能为空")
    private String paymentMethod;
}
