package cn.zswltech.mithras.dto.workbench.chart.sub;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 表格表头对象VO
 * @Author: zhaozhengkang
 **/
@Data
@ApiModel("表格表头对象VO")
public class FormColumnVO {

    @ApiModelProperty("父级表头名称")
    private String parentTitle;

    @ApiModelProperty("表头名称")
    private String title;

    @ApiModelProperty("表头下标code: indexStatus")
    private String dataIndex;
}
