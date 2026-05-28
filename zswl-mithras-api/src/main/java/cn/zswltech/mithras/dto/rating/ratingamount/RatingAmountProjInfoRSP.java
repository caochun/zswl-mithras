package cn.zswltech.mithras.dto.rating.ratingamount;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class RatingAmountProjInfoRSP {

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

    @ApiModelProperty(value = "评估主体id")
    private Long evaluationSubjectId;

    @ApiModelProperty(value = "评估主体名称")
    private String evaluationSubjectName;

    @ApiModelProperty(value = "评估主体区域")
    private String evaluationSubjectAreaName;

    @ApiModelProperty(value = "评估主体营业收入")
    private Long evaluationSubjectOperatingIncome;

    @ApiModelProperty(value = "租赁类型")
    private String leaseTypes;

    @ApiModelProperty(value = "资金用途")
    private String fundsPurpose;

    @ApiModelProperty(value = "行业分类")
    private String projectClassify;

    @ApiModelProperty(value = "地区分类")
    private String regionalProjectClassify;

    private String bizType;



}
