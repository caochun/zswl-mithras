package cn.zswltech.mithras.dto.workbench.chart.sub;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 图表数据返回对象VO
 * @Author: zhaozhengkang
 **/
@Data
@ApiModel("图表数据返回对象VO")
@AllArgsConstructor
@NoArgsConstructor
public class ChartDataVO {

    @ApiModelProperty("数据类型(名称)")
    private String dataType;

    @ApiModelProperty("图表类型:bar-柱状, line-折线")
    private String chartType;

    @ApiModelProperty("图表数据集合")
    private List<ChartBaseDataVO> list;
}
