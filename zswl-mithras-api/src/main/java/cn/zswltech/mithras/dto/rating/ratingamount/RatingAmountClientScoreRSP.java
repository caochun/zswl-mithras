package cn.zswltech.mithras.dto.rating.ratingamount;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingAmountClientScoreRSP {

    @ApiModelProperty(value = "评估主体名称")
    private String evaluationSubjectName;

    @ApiModelProperty(value = "评估主体信用代码")
    private String evaluationSubjectUscCode;

    @ApiModelProperty(value = "评估主体等级")
    private String finalScore;

}
