package cn.zswltech.mithras.api.payment.version;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/19 10:33
 */
@Data
@ApiModel(value = "付款申请提交审批-请求体")
public class PaymentEffectREQ {
    @NotNull
    @ApiModelProperty("申请id")
    public Long id;
}
