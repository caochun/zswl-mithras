package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CollectionFlowCenterBusinessCollectionExportListREQ {

    @ApiModelProperty("应收日期")
    private String planCollectionDate;

    @ApiModelProperty("收款明细Id")
    private List<Integer> collectionIds;
}
