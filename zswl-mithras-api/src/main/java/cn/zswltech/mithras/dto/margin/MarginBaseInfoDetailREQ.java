package cn.zswltech.mithras.dto.margin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @create: 2022-08-16
 **/
@Data
@ApiModel("保证金明细-请求体")
public class MarginBaseInfoDetailREQ {

    @ApiModelProperty("保证金id")
    private Long id;


}
