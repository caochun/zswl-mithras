package cn.zswltech.mithras.dto.rating.ratingamount;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RatingAmountAddREQ {

    @ApiModelProperty(value = "评审id")
    @NotNull(message = "评审id不得为空")
    private Long projReviewId;

    @ApiModelProperty(value = "项目名称")
    @NotNull(message = "项目名称不得为空")
    private String projName;

    @ApiModelProperty("项目编号")
    @NotNull(message = "项目编号不得为空")
    private String projCode;

    @ApiModelProperty(value = "主承租人ID")
    @NotNull(message = "主承租人ID不得为空")
    private Long clientId;

//    @ApiModelProperty(value = "主承租人名称")
//    @NotNull(message = "主承租人名称不得为空")
//    private String mainTenantryName;

    @ApiModelProperty(value = "主承租人统一社会信用代码")
    @NotNull(message = "主承租人统一社会信用代码不得为空")
    private String clientUscCode;

    @ApiModelProperty(value = "评估主体ID")
    @NotNull(message = "评估主体ID不得为空")
    private Long evaluationSubjectId;

    @ApiModelProperty(value = "评估主体名称")
    @NotNull(message = "评估主体名称不得为空")
    private String evaluationSubjectName;

//    @ApiModelProperty(value = "评估主体统一社会信用代码")
//    @NotNull(message = "评估主体统一社会信用代码不得为空")
//    private String evaluationSubjectUscCode;

    @ApiModelProperty("模型名称")
    @NotNull(message = "模型名称不得为空")
    private String name;

    @ApiModelProperty("模型编号")
    @NotNull(message = "模型编号不得为空")
    private String code;

    @ApiModelProperty("债项类型：是否为项目制")
    @NotNull(message = "是否为项目制不得为空")
    private Boolean projSystem;

    @ApiModelProperty("债项类型：是否有实质性租赁物")
    @NotNull(message = "是否有实质性不得为空")
    private Boolean materialLeaseItem;


}
