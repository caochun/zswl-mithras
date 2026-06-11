package cn.zswltech.mithras.dto.rating.ratingclient;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class RatingClientAddRSP {

    @ApiModelProperty(value = "评级id")
    private Long id;


}
