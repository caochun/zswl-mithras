package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/12/14
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PaymentActualDetailModifyReq extends PaymentActualDetailAddReq {
    @NotNull(message = "付款核销记录id不能为空")
    @ApiModelProperty(value = "付款核销记录id")
    private Long id;
}
