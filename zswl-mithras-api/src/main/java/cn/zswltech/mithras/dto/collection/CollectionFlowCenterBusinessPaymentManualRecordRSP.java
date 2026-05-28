package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bigbear
 * @date 2024/10/14 10:38
 * @description
 */
@Data
public class CollectionFlowCenterBusinessPaymentManualRecordRSP {

    @ApiModelProperty(value = "付款待核销记录ID")
    private Long paymentActualDetailId;

    @ApiModelProperty(value = "现金流编号")
    private String cashFlowCode;

    @ApiModelProperty(value = "已核销金额")
    private Long writeOffedAmount;

    @ApiModelProperty(value = "应付金额")
    private Long payInAmount;
}
