package cn.zswltech.mithras.dto.margin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @create: 2022-08-16
 **/
@Data
public class MarginwriteOffListREQ {
    @ApiModelProperty("保证金id")
    private Long id;
}
