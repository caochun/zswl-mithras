package cn.zswltech.mithras.dto.workbench;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/9 13:50
 */
@Data
@ApiModel("工作台-卡片指标选择")
public class CardMetricChooseDto {

    @ApiModelProperty("指标名称")
    private String metricName;

    @ApiModelProperty("是否选中，ture选中，false未选中")
    private Boolean selected;
}
