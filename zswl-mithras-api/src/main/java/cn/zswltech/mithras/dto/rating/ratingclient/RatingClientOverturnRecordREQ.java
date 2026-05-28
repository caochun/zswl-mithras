package cn.zswltech.mithras.dto.rating.ratingclient;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RatingClientOverturnRecordREQ {


    @ApiModelProperty("评级id")
    private Long id;

}
