package cn.zswltech.mithras.dto.riskcontrol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description 预警监控管理
 * @date 2023-02-08
 */
@Data
@ApiModel("预警监控指标列表-返回体")
public class RiskControlStrategyListRsp {
    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "指标编号")
    private String metricCode;
    @ApiModelProperty(value = "指标类型")
    private String metricType;
    @ApiModelProperty(value = "指标类别")
    private String metricCategory;
    @ApiModelProperty(value = "指标名称")
    private String metricName;
    @ApiModelProperty(value = "计算逻辑")
    private String computationalLogic;
    @ApiModelProperty(value = "预警值1")
    private Long earlyWarningValueOne;
    @ApiModelProperty(value = "限定值1")
    private Long limitValueOne;
    @ApiModelProperty(value = "当前值1")
    private Long currentValueOne;
    @ApiModelProperty(value = "单位1")
    private String valueUnitOne;
    @ApiModelProperty(value = "预警值2")
    private Long earlyWarningValueTwo;
    @ApiModelProperty(value = "当前值2")
    private Long currentValueTwo;
    @ApiModelProperty(value = "限定值2")
    private Long limitValueTwo;
    @ApiModelProperty(value = "单位2")
    private String valueUnitTwo;
    @ApiModelProperty(value = "更新时间")
    private String updateTime;
    @ApiModelProperty(value = "空值原因")
    private String nullReason;
    @ApiModelProperty(value = "预警状态（1启用，0禁用）")
    private Integer earlyWarningState;
    @ApiModelProperty(value = "比较方式1")
    private String comparisonMethodOne;
    @ApiModelProperty(value = "比较方式2")
    private String comparisonMethodTwo;
}
