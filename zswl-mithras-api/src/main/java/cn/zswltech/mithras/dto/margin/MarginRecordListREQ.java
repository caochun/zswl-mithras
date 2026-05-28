package cn.zswltech.mithras.dto.margin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @create: 2022-08-16
 **/
@Data
public class MarginRecordListREQ {
    @ApiModelProperty("保证金id")
    private Long id;

    @ApiModelProperty("记录类型：退款 or 收款")
    private String recordType;

}
