package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @create: 2022-08-16
 **/
@Data
public class CollectionRecordListREQ {
    @ApiModelProperty("收款核销id")
    private Long id;
}
