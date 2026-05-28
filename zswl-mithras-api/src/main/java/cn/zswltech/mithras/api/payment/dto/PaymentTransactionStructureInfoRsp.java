package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("付款申请交易结构信息-出参")
@Data
public class PaymentTransactionStructureInfoRsp {

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("交易结构类型")
    private String clientType;

    @ApiModelProperty("存量风险敞口（毫厘）")
    private Long stockRiskExposure;

    @ApiModelProperty("交易结构类型")
    private String transactionStructureType;

    @ApiModelProperty(value = "舆情数量")
    private Integer count;
}
