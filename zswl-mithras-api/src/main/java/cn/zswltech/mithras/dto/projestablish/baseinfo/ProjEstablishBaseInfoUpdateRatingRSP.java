package cn.zswltech.mithras.dto.projestablish.baseinfo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ProjEstablishBaseInfoUpdateRatingRSP {
    @ApiModelProperty("评估主体评级")
    private String ratingFinalScore;

    @ApiModelProperty(value = "客户评级id")
    private Long ratingClientId;

    @ApiModelProperty("主承租人评级")
    private String mainLesseeFinalScore;

    @ApiModelProperty(value = "主承租人评级id")
    private Long mainLesseeRatingId;
}
