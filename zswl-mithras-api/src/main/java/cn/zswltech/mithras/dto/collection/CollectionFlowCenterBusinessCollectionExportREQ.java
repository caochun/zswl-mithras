package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class CollectionFlowCenterBusinessCollectionExportREQ {

    @ApiModelProperty("应收日期")
    private String planCollectionDate;


}
