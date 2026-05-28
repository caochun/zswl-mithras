package cn.zswltech.mithras.dto.workbench.chart;

import cn.zswltech.mithras.dto.workbench.chart.sub.ChartDataVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 折线图表VO
 * @Author: zhaozhengkang
 **/
@Data
@ApiModel("折线图表VO")
@AllArgsConstructor
@NoArgsConstructor
public class LineChartValueVO {

    @ApiModelProperty("表名称")
    private String title;

    @ApiModelProperty(value = "Y轴值集合(级别)", example = "A-,A,A+,AA,AAA,B-,B,B+,BB,BBB,C-,C,C+,CC,CCC,D-,D,D+,DD,DDD,E-,E,E+,EE,EEE")
    private List<String> y_axis;

    @ApiModelProperty("表数据")
    private List<ChartDataVO> data;
}
