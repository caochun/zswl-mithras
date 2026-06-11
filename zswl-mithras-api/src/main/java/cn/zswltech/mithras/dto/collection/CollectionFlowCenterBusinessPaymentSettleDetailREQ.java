package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CollectionFlowCenterBusinessPaymentSettleDetailREQ {

    @ApiModelProperty("现金流项目 PaymentFlowItemEnum")
    @NotNull(message = "现金流项目不能为空")
    private String cashFlowItem;

    @ApiModelProperty("现金流项目")
    @NotNull(message = "付款ID不能为空")
    private Long paymentId;

}
