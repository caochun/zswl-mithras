package cn.zswltech.mithras.dto.rating;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RatingAccessCheckRSP {


    @ApiModelProperty(value = "客户评级结果准入")
    private Boolean clientRankAccess;

    @ApiModelProperty(value = "政信类客户区域准入")
    private Boolean clientAreaAccess;

}
