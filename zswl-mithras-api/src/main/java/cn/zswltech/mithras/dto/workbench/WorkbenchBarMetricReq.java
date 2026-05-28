package cn.zswltech.mithras.dto.workbench;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/9 17:14
 */
@ApiModel("工作台-柱状图指标列表-请求体")
@Data
public class WorkbenchBarMetricReq extends WorkbenchMetricReq {

    @ApiModelProperty(value = "时间范围枚举, 本周(WEEKLY), 本月(MONTHLY), 本季度(QUARTERLY), 本年(YEARLY)")
    private String workbenchMetricTimeScope;
}
