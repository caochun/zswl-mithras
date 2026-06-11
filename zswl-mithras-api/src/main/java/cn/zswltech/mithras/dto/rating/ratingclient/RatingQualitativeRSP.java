package cn.zswltech.mithras.dto.rating.ratingclient;

import cn.zswltech.mithras.dto.rating.RatingParamFieldRSP;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 定性指标
 */
@Data
public class RatingQualitativeRSP {

    @ApiModelProperty(value = "指标编号")
    private String fieldCode;

    @ApiModelProperty(value = "指标名称")
    private String fieldName;

    @ApiModelProperty(value = "指标档位")
    private Object value;

    @ApiModelProperty(value = "二级指标档位")
    private Object secondValue;

    @ApiModelProperty(value = "分组名称")
    private String dataType;

    @ApiModelProperty(value = "列表数据")
    private List<RatingParamFieldRSP.DictionaryDTO> enumList;

    @ApiModelProperty(value = "描述")
    private String fieldComment;

    @ApiModelProperty(value = "指标得分")
    private String fieldScore;

    @ApiModelProperty("审批状态")
    private Boolean approvalStatus;

    @ApiModelProperty("数据是否变化-流程中被退回，再发起时返回")
    private Boolean isChange = false;

    @ApiModelProperty("审批意见")
    private String approvalOpinion;

}
