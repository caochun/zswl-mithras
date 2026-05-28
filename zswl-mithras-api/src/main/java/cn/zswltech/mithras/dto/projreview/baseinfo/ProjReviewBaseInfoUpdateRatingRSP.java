package cn.zswltech.mithras.dto.projreview.baseinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ProjReviewBaseInfoUpdateRatingRSP {


    @ApiModelProperty(value = "客户评级id")
    private Long ratingClientId;

    @ApiModelProperty(value = "主承租人评级id")
    private Long ratingMainLesseeId;

    @ApiModelProperty(value = "债项评级id")
    private Long ratingAmountId;

    @ApiModelProperty("评估主体评级")
    private String ratingFinalScore;

    @ApiModelProperty("主承租人评级")
    private String ratingMainLesseeScore;

    @ApiModelProperty("债项限额")
    private String ratingQuota;

}
