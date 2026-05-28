package cn.zswltech.mithras.dto.rating.ratingamount;

import cn.zswltech.mithras.dto.rating.RatingParamFieldRSP;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 评估基准
 */
@Data
public class RatingEvaluateBaseRSP {


    @ApiModelProperty(value = "指标名称")
    private String fieldName;

    @ApiModelProperty(value = "分组名称")
    private String groupName;

    @ApiModelProperty(value = "指标值")
    private Object value;

    @ApiModelProperty(value = "指标分数")
    private String fieldScore;

    @ApiModelProperty(value = "描述")
    private String fieldComment;

    @ApiModelProperty(value = "类型")
    private String dataType;

    @ApiModelProperty(value = "可选选项")
    private List<RatingParamFieldRSP.DictionaryDTO> enumList;

    @ApiModelProperty("数据是否变化-流程中被退回，再发起时返回")
    private Boolean isChange = false;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("计算公式")
    private String formal;

    @ApiModelProperty("审批状态")
    private Boolean approvalStatus;

    @ApiModelProperty("审批意见")
    private String approvalOpinion;

}
