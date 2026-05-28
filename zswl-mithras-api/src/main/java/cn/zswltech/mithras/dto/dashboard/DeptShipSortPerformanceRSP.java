package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yangxiong
 * @date 2024/6/24/15:29
 * @description
 */
@Data
@ApiModel(value = "部门间业绩排名响应体")
@EqualsAndHashCode(callSuper = true)
public class DeptShipSortPerformanceRSP extends DeptSortPerformanceDTO{

    @ApiModelProperty(value = "投放金额（万元")
    private String advertisingAmount;

    @ApiModelProperty(value = "收入金额（万元")
    private String incomeAmount;

    @ApiModelProperty(value = "利润金额（万元")
    private String profitAmount;

    @ApiModelProperty(value = "营业收入目标（万元）")
    private String revenueTarget;

    @ApiModelProperty(value = "利润目标（万元）")
    private String profitTarget;

    @ApiModelProperty(value = "投放目标（万元）")
    private String advertisingTarget;
}
