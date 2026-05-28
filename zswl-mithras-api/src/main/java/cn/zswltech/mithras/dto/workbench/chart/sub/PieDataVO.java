package cn.zswltech.mithras.dto.workbench.chart.sub;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 饼图数据对象VO
 * @Author: zhaozhengkang
 **/
@Data
@ApiModel("饼图基础信息对象VO")
@AllArgsConstructor
@NoArgsConstructor
public class PieDataVO {

    @ApiModelProperty(value = "块-名称")
    private String name;

    @ApiModelProperty(value = "块-值")
    private String value;

    @ApiModelProperty(value = "块-占比")
    private String rate;
}
