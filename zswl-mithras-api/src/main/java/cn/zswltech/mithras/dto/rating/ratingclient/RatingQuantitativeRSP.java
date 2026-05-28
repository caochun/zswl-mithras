package cn.zswltech.mithras.dto.rating.ratingclient;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 定量指标
 */
@Data
public class RatingQuantitativeRSP {

    @ApiModelProperty(value = "序号")
    private long index;

    @ApiModelProperty(value = "指标名称")
    private String fieldName;

    @ApiModelProperty(value = "描述")
    private String fieldComment;

    @ApiModelProperty(value = "数据类型")
    private String dataType;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("计算公式")
    private String formal;

    @ApiModelProperty("取数方式 RatingFetchMethodEnum")
    private String fetchMethod;

    @ApiModelProperty(value = "指标字段值")
    private Object value;

    @ApiModelProperty(value = "指标得分")
    private String fieldScore;

    @ApiModelProperty(value = "数据时点")
    private LocalDateTime date;

    @ApiModelProperty("数据是否变化-流程中被退回，再发起时返回")
    private Boolean isChange = false;

    @ApiModelProperty("审批状态")
    private Boolean approvalStatus;

    @ApiModelProperty("审批意见")
    private String approvalOpinion;

    @ApiModelProperty("是否为系统取数的区域模型指标")
    private Boolean isAreaModelIndex = false;

}
