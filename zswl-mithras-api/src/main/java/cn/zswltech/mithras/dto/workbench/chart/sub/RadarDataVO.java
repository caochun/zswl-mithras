package cn.zswltech.mithras.dto.workbench.chart.sub;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/10 14:04
 */
@Data
@ApiModel("雷达图数据对象VO")
@AllArgsConstructor
@NoArgsConstructor
public class RadarDataVO {
    @ApiModelProperty("值名称")
    private String name;
    @ApiModelProperty("值")
    private String value;
    @ApiModelProperty("单位")
    private String unitDisplay;
}
