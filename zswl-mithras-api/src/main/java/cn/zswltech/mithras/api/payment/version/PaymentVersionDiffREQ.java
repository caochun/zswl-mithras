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
@ApiModel("付款申请版本差异比较-入参")
@Data
public class PaymentVersionDiffREQ {
    @ApiModelProperty("版本id")
    @NotNull
    private Long id;
}
