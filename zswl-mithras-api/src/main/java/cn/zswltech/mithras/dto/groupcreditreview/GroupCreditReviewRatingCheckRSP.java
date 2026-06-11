package cn.zswltech.mithras.dto.groupcreditreview;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author dingqi
 * @date 2025/1/15
 * @description
 */
@Data
public class GroupCreditReviewRatingCheckRSP {
    @ApiModelProperty("是否完成了客户评级")
    private Boolean ratingClientIsDone = false;

    private Long clientId;
}
