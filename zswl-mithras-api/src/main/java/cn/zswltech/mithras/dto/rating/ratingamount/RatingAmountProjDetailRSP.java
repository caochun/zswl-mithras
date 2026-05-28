package cn.zswltech.mithras.dto.rating.ratingamount;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RatingAmountProjDetailRSP {

    @ApiModelProperty(value = "债项评级id")
    private Long id;

    @ApiModelProperty("债项评级参考额度")
    private String ratingQuota;

}
