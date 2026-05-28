package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @create: 2022-08-16
 **/
@Data
public class CollectionRecordDetailREQ {
    @ApiModelProperty("核销记录id")
    private Long id;
}
