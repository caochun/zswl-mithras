package cn.zswltech.mithras.dto.rating.ratingamount;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RatingAmountDeleteREQ {

    @ApiModelProperty(value = "评级id")
    @NotNull(message = "id不得为空")
    private Long id;


}
