package cn.zswltech.mithras.dto.rating.ratingclient;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RatingClientAbstractREQ {

    @ApiModelProperty("评级id")
    @NotNull(message = "评级id不得为空")
    private Long id;

}
