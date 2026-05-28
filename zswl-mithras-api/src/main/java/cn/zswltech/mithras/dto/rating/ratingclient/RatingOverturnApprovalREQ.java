package cn.zswltech.mithras.dto.rating.ratingclient;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RatingOverturnApprovalREQ {

    @ApiModelProperty(value = "评级id")
    @NotNull(message = "id不得为空")
    private Long id;

    @ApiModelProperty(value = "是否同意推翻")
    @NotNull(message = "是否同意推翻不得为空")
    private boolean agreeOverturn;


}
