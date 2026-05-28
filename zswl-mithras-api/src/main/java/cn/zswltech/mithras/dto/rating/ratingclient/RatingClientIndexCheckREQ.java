package cn.zswltech.mithras.dto.rating.ratingclient;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RatingClientIndexCheckREQ {

    @ApiModelProperty(value = "id")
    @NotNull(message = "id不得为空")
    private Long id;

}
