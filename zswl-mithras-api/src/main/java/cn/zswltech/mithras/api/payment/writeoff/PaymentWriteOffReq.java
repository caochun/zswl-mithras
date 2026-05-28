package cn.zswltech.mithras.api.payment.writeoff;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/17 11:30
 */
@ApiModel("核销付款申请-入参")
@Data
public class PaymentWriteOffReq {
    @ApiModelProperty("付款申请- id")
    private Long id;
}
