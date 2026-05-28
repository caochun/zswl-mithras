package cn.zswltech.mithras.dto.riskcontrol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/15 14:19
 */
@Data
@ApiModel("付款核销拦截请求体")
public class PaymentApplyInterceptReq {
    @ApiModelProperty("付款申请id")
    @NotNull(message = "付款申请id不能为空")
    private Long paymentId;

    @ApiModelProperty("合同id")
    @NotNull(message = "合同id不能为空")
    private Long contractId;
}
