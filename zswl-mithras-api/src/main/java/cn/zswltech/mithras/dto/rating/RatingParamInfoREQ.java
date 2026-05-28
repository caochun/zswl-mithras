package cn.zswltech.mithras.dto.rating;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RatingParamInfoREQ {

    @ApiModelProperty("债项评级id")
    @NotNull(message = "id不得为空")
    private Long id;


}
