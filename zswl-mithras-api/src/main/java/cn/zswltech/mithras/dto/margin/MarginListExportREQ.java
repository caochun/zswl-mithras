package cn.zswltech.mithras.dto.margin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @create: 2022-08-17
 **/
@Data
public class MarginListExportREQ {
    @ApiModelProperty("收款核销ids")
    private List<Long> ids;

}
