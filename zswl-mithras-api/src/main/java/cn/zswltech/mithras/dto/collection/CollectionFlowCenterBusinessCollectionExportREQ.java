package cn.zswltech.mithras.dto.collection;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CollectionFlowCenterBusinessCollectionExportREQ {

    @ApiModelProperty("应收日期")
    private String planCollectionDate;


}
