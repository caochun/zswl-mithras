package cn.zswltech.mithras.dto.rating.ratingclient;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RatingClientInfoREQ {

    @ApiModelProperty(value = "客户id")
    private Long clientId;

    @ApiModelProperty(value = "id")
    private Long id;
}
