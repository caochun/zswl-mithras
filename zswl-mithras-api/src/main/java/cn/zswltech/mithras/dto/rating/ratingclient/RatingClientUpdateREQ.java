package cn.zswltech.mithras.dto.rating.ratingclient;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class RatingClientUpdateREQ extends RatingClientAddREQ{

    @ApiModelProperty("评级id")
    private Long id;

}
