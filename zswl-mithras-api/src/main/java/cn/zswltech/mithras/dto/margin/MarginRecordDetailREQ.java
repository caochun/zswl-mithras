package cn.zswltech.mithras.dto.margin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @create: 2022-08-16
 **/

@Data
public class MarginRecordDetailREQ {
    @ApiModelProperty("记录id")
    private Long id;
}
