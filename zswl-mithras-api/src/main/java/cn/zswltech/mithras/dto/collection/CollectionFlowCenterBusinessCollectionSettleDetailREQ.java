package cn.zswltech.mithras.dto.collection;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CollectionFlowCenterBusinessCollectionSettleDetailREQ extends PageReq {

    @ApiModelProperty("收款")
    @NotNull(message = "收款ID不能为空")
    private Long collectionId;

}
