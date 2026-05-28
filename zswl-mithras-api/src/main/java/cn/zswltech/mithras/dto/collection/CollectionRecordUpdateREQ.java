package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @create: 2022-08-16
 **/
@Data
public class CollectionRecordUpdateREQ {

    @ApiModelProperty("记录id")
    private Long id;

    @ApiModelProperty("核销状态")
    private String writeOff;

}
