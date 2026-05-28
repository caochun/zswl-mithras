package cn.zswltech.mithras.dto.workbench.chart;

import cn.zswltech.mithras.dto.workbench.chart.sub.RadarDataVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/10 14:01
 */
@Data
@ApiModel("雷达图类型对象VO")
@AllArgsConstructor
@NoArgsConstructor
public class RadarChartValueVO {
    @ApiModelProperty("雷达图标题")
    private String title;
    @ApiModelProperty("雷达图数据，key为max的表示最大值，其余的为雷达图的值")
    private Map<String, List<RadarDataVO>> data;


    @ApiModelProperty(value = "maxName")
    private String maxName;
}

