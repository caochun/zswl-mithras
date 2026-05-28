package cn.zswltech.mithras.dto.rating.ratingamount;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RatingAmountDetailREQ {

    @ApiModelProperty(value = "债项评级id")
    @NotNull(message = "债项评级id不得为空")
    private Long id;


}
