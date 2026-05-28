package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bigbear
 * @date 2024/12/23 10:39
 * @className PaymentCheckApplyAmountRsp
 * @description
 */
@Data
public class PaymentCheckApplyAmountRsp {

    @ApiModelProperty(value = "项目批复金额")
    private Long approvedAmount;

    @ApiModelProperty(value = "是否超过项目批复金额")
    private Boolean isOverApprovedAmount;

    @ApiModelProperty(value = "合同金额")
    private Long contractAmount;

    @ApiModelProperty(value = "是否超过合同金额")
    private Boolean isOverContractAmount;

    @ApiModelProperty(value = "二次确认提示")
    private Boolean needConfirmTips;

    @ApiModelProperty(value = "二次确认提示词")
    private String tipMessage;
}
