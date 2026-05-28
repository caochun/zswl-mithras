package cn.zswltech.mithras.api.payment.writeoff;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/16 13:43
 */
@ApiModel("付款核销详情接口-入参")
@Data
public class PaymentWriteOffDetailReq {
    @ApiModelProperty("付款id")
    private Long paymentId;
    @ApiModelProperty("合同id")
    private Long contractId;
}
