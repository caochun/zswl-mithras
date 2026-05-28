package cn.zswltech.mithras.dto.rating.ratingamount;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingAmountAddCheckRSP {

    @ApiModelProperty(value = "是否已经存在评级")
    private boolean isExist;

}
