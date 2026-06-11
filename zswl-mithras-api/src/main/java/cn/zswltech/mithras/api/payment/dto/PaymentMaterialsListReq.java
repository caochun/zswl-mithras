package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/15 10:06
 */
@Data
@ApiModel("放款材料清单-请求体")
public class PaymentMaterialsListReq {
    @NotNull(message = "付款申请id不能为空")
    @ApiModelProperty("付款申请id")
    private Long paymentId;

    @ApiModelProperty("资料类型")
    private String materialsType;


}

