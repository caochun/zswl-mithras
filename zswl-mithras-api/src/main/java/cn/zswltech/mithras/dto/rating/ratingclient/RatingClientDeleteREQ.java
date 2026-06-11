package cn.zswltech.mithras.dto.rating.ratingclient;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class RatingClientDeleteREQ {

    @ApiModelProperty("评级id")
    private Long id;

}
