package cn.zswltech.mithras.dto.rating.ratingamount;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RatingAmountAccessCheckREQ {

    @ApiModelProperty(value = "主承租人ID")
    @NotNull(message = "主承租人ID不得为空")
    private Long clientId;

    @ApiModelProperty("债项类型：是否有实质性租赁物")
    @NotNull(message = "是否有实质性不得为空")
    private Boolean materialLeaseItem;

    @ApiModelProperty(value = "评审id")
    @NotNull(message = "评审id不得为空")
    private Long projReviewId;

    @ApiModelProperty(value = "评估主体ID")
    @NotNull(message = "评估主体ID不得为空")
    private Long evaluationSubjectId;




}
