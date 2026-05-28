package cn.zswltech.mithras.api.payment.dto.pubinfo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author bigbear
 * @date 2025/2/24 10:28
 * @description
 */
@Data
public class PublicInfoCopyREQ {

    @ApiModelProperty(value = "付款申请ID")
    @NotNull(message = "付款申请ID不能为空")
    private Long paymentId;
}
