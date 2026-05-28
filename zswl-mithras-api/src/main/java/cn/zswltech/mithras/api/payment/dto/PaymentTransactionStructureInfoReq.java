package cn.zswltech.mithras.api.payment.dto;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel("付款申请交易结构信息-入参")
@Data
public class PaymentTransactionStructureInfoReq extends VersionBaseREQ {
    @ApiModelProperty("付款申请ID")
    @NotNull(message = "paymentId为空")
    private Long paymentId;

}
