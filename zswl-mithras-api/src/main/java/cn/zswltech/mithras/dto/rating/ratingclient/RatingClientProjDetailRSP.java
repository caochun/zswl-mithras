package cn.zswltech.mithras.dto.rating.ratingclient;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RatingClientProjDetailRSP {

    @ApiModelProperty(value = "客户评级id")
    private Long id;

    @ApiModelProperty("评估主体评级")
    private String ratingFinalScore;

}
