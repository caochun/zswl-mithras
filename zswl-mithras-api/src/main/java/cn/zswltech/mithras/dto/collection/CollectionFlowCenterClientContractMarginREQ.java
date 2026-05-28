package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CollectionFlowCenterClientContractMarginREQ {

    @ApiModelProperty("客户ID")
    @NotNull(message = "客户ID不能为空")
    private Long clientId;

}
