package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class ClientUnifiedApplyCreditREQ  {

    @ApiModelProperty("客户ID")
    @NotNull(message = "客户id不能为空")
    private Long clientId;

}
