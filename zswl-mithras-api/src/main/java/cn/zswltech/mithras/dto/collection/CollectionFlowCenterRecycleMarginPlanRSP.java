package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CollectionFlowCenterRecycleMarginPlanRSP {

    @ApiModelProperty("回收日期")
    private LocalDate planCollectionDate;

}
