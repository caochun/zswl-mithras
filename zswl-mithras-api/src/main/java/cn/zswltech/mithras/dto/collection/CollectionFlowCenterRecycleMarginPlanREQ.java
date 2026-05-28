package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CollectionFlowCenterRecycleMarginPlanREQ {

    @ApiModelProperty("收款ID")
    @NotNull(message = "收款ID不能为空")
    private Long collectionId;


}
