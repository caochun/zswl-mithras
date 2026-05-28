package cn.zswltech.mithras.dto.liquidityrisk;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @create: 2023-05-15
 **/

@Data
public class OutflowTableRsp<X,Y> {
    @ApiModelProperty("横轴")
    private X x;
    @ApiModelProperty("纵轴")
    private Y y;

}
