package cn.zswltech.mithras.dto.workbench.chart.sub;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 图表数据对象VO
 * @Author: zhaozhengkang
 **/
@Data
@ApiModel("图表数据对象VO")
@AllArgsConstructor
@NoArgsConstructor
public class ChartBaseDataVO {

    @ApiModelProperty("横向坐标展示名称")
    private String name;

    @ApiModelProperty("纵向坐标展示值")
    private String value;

    @ApiModelProperty("鼠标悬停展示值")
    private String hoverValue;

    @ApiModelProperty("单位")
    private String unitDisplay;
}
