package cn.zswltech.mithras.dto.groupcreditreview.baseinfo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GroupCreditReviewInfoUpdateRatingRSP {
    @ApiModelProperty(value = "客户评级id")
    private Long clientRatingScoreId;

    @ApiModelProperty("客户评级")
    private String clientRatingScore;

}
