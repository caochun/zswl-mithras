package cn.zswltech.mithras.dto.projestablish.baseinfo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ProjEstablishBaseInfoUpdateRatingREQ {


    @ApiModelProperty(value = "id")
    @NotNull(message = "id不得为空")
    private Long id;

    @ApiModelProperty(value = "客户评级id")
    private Long ratingClientId;
}
