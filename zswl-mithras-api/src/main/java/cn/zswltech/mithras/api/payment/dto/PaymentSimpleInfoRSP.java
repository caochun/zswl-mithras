package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2023/4/3
 * @description
 */
@Data
@ApiModel("付款信息（简略）-返回体")
public class PaymentSimpleInfoRSP {
    @ApiModelProperty("付款id")
    private Long paymentId;

    @ApiModelProperty("付款编号")
    private String paymentCode;
}
