package cn.zswltech.mithras.dto.rating.ratingclient;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RatingClientOverturnREQ {

    @ApiModelProperty(value = "评级id")
    @NotNull(message = "id不得为空")
    private Long id;

    @ApiModelProperty(value = "评级认定结果")
    private String finalScore;

//    @ApiModelProperty(value = "是否为校验")
//    private Boolean onlyCheck = false;
}
