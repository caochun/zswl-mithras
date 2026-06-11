package cn.zswltech.mithras.dto.rating.ratingclient;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RatingClientEffectREQ {

    @ApiModelProperty(value = "id")
    @NotNull(message = "id不得为空")
    private Long id;

    @ApiModelProperty(value = "评级调整的意见")
    private String adjustOpinion;


}
