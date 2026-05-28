package cn.zswltech.mithras.dto.workbench;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhaozhengkang
 * @description 工作台-卡片指标
 * @date 2023-05-09
 */
@Data
@ApiModel("工作台-卡片指标列表-返回体")
@Accessors(chain = true)
public class WorkbenchCardMetricListRsp {
    @ApiModelProperty(value = "指标名称")
    private String metricName;
    @ApiModelProperty(value = "值")
    private String value;
    @ApiModelProperty(value = "指标单位")
    private String unit;
    @ApiModelProperty(value = "指标单位")
    private String unitDisplay;
    @ApiModelProperty(value = "是否为饼图")
    private Boolean pieChart;
}
