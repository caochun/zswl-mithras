package cn.zswltech.mithras.dto.projreview.baseinfo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ProjReviewBaseInfoUpdateRatingREQ {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    @NotNull(message = "id不能为空")
    private Long id;

    @ApiModelProperty(value = "客户评级id")
    private Long ratingClientId;

    @ApiModelProperty(value = "债项评级id")
    private Long ratingAmountId;

}
