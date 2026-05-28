package cn.zswltech.mithras.dto.rating.ratingamount;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingAmountInfoRSP {

    @ApiModelProperty(value = "评审id")
    private Long projReviewId;

    @ApiModelProperty(value = "项目名称")
    private String projName;

    @ApiModelProperty(value = "项目编号")
    private String projCode;

    @ApiModelProperty(value = "主承租人id")
    private Long clientId;

    @ApiModelProperty(value = "主承租人名称")
    private String clientName;

    @ApiModelProperty(value = "主承租人统一社会信用代码")
    private String clientUscCode;

    @ApiModelProperty(value = "评估主体下拉框")
    private List<EvaluationSubject> evaluationSubjectList;

    @ApiModelProperty(value = "评估主体ID")
    private Long evaluationSubjectId;

    @ApiModelProperty(value = "评估主体名称")
    private String evaluationSubjectName;

    @ApiModelProperty(value = "评估主体统一社会信用代码")
    private String evaluationSubjectUscCode;


    @Data
    public static class EvaluationSubject{
        @ApiModelProperty("客户id")
        private Long evaluationSubjectId;

        @ApiModelProperty("客户名称")
        private String evaluationSubjectName;

        @ApiModelProperty("客户统一社会信用代码")
        private String evaluationSubjectUscCode;

    }

}
