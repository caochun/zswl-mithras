package cn.zswltech.mithras.dto.rating.ratingamount;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RatingAmountUpdateRSP {

    @ApiModelProperty(value = "债项评级id")
    private Long id;


}
