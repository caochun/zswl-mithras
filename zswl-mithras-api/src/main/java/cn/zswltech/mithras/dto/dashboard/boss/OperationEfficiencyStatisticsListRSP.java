package cn.zswltech.mithras.dto.dashboard.boss;

import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangxiong
 * @date 2024/5/15/17:33
 * @description
 */
@Data
public class OperationEfficiencyStatisticsListRSP {
    @ApiModelProperty(value = "阶段名称")
    private String stageName;

    @ApiModelProperty(value = "历史平均")
    private ValueUnitDTO average;

    @ApiModelProperty(value = "本月平均")
    private ValueUnitDTO averageThisMonth;

    @ApiModelProperty(value = "环比（上月）")
    private ValueUnitDTO chainRatio;

    @ApiModelProperty(value = "同比（上年）")
    private ValueUnitDTO yearOnYearBasis;

    @ApiModelProperty(value = "数量")
    private ValueUnitDTO quantity;

    @ApiModelProperty(value = "融资额")
    private ValueUnitDTO financeAmount;
}
