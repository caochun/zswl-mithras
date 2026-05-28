package cn.zswltech.mithras.dto.rating.ratingamount;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RatingAmountAddRSP {

    @ApiModelProperty(value = "债项评级id")
    private Long id;

    @ApiModelProperty(value = "是否已经存在评级")
    private boolean isExist;

}
