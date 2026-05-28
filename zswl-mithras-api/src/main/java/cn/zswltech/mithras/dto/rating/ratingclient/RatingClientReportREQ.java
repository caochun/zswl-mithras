package cn.zswltech.mithras.dto.rating.ratingclient;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RatingClientReportREQ {

    @ApiModelProperty(value = "评级id")
    @NotNull(message = "客户评级id不得为空")
    private Long id;

}
