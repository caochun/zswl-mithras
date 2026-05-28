package cn.zswltech.mithras.dto.workbench.chart;

import cn.zswltech.mithras.dto.workbench.chart.sub.PieDataVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 饼图类型对象VO
 * @Author: zhaozhengkang
 **/
@Data
@ApiModel("饼图类型对象VO")
@AllArgsConstructor
@NoArgsConstructor
public class PieChartValueVO {

    @ApiModelProperty(value = "饼图标题")
    private String title;

    @ApiModelProperty(value = "饼图数据")
    private List<PieDataVO> data;
}
