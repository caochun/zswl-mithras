package cn.zswltech.mithras.dto.payment.lib;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description payment_policy_info
 * @author zhaozhengkang
 * @date 2022-09-13
 */
@Data
@ApiModel("payment_policy_info批量删除-请求体")
public class PaymentPolicyInfoRemoveBatchREQ {

    @NotNull(message = "保单id不能为空")
    @ApiModelProperty("ids")
    private List<Long> ids;

    @NotNull(message = "付款id不能为空")
    @ApiModelProperty("paymentId必填，用于权限校验")
    private Long paymentId;

}
