package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/9/15
 * @description
 */
@Data
@ApiModel("付款-对方账户列表-请求体")
public class PaymentBankAccountListReq {
    @NotNull(message = "付款id不能为空")
    @ApiModelProperty("付款id")
    private Long id;

    @NotNull(message = "客户id不能为空")
    @ApiModelProperty("客户id")
    private Long clientId;
}
