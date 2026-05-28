package cn.zswltech.mithras.dto.workbench.chart;

import cn.zswltech.mithras.dto.workbench.chart.sub.FormColumnVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @Description: 表格图表对象VO
 * @Author: zhaozhengkang
 **/
@Data
@ApiModel("表格图表对象VO")
@AllArgsConstructor
@NoArgsConstructor
public class FormValueVO {

    @ApiModelProperty("表头列集合")
    private List<FormColumnVO> columns;

    /**
     * map<key=column对象的dataIndex, value=需要填写的数据>
     */
    @ApiModelProperty("表数据集合")
    private List<Map<Object, Object>> dataSources;
}
