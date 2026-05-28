package cn.zswltech.mithras.dto.payment.lib;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
/**
 * @description payment_policy_info
 * @author zhaozhengkang
 * @date 2022-09-13
 */
@Data
@ApiModel("payment_policy_info删除-请求体")
public class PaymentPolicyInfoRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

    @NotNull
    @ApiModelProperty("paymentId必填，用于权限校验")
    private Long paymentId;

}
