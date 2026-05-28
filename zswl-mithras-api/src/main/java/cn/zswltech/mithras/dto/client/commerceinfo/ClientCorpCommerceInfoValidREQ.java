package cn.zswltech.mithras.dto.client.commerceinfo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author bigbear
 * @date 2025/2/20 09:48
 * @description
 */
@Data
public class ClientCorpCommerceInfoValidREQ {

    @ApiModelProperty(value = "客户id")
    @NotNull(message = "客户id不能为空")
    private Long clientId;
}
