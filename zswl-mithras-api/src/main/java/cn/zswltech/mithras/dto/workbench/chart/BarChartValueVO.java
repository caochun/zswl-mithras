package cn.zswltech.mithras.dto.workbench.chart;

import cn.zswltech.mithras.dto.workbench.chart.sub.ChartDataVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 柱状图表VO
 * @Author: zhaozhengkang
 **/
@Data
@ApiModel("柱状图表VO")
@AllArgsConstructor
@NoArgsConstructor
public class BarChartValueVO {

    @ApiModelProperty("表名称")
    private String title;

    @ApiModelProperty("表数据")
    private List<ChartDataVO> data;
}
