package cn.zswltech.mithras.dto.rating.ratingamount;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RatingAmountAddCheckREQ {

    @ApiModelProperty(value = "评审id")
    @NotNull(message = "评审id不得为空")
    private Long projReviewId;

    @ApiModelProperty(value = "评估主体ID")
    @NotNull(message = "评估主体ID不得为空")
    private Long evaluationSubjectId;


}
