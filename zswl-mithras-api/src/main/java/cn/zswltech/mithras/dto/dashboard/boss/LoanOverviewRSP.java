package cn.zswltech.mithras.dto.dashboard.boss;

import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yangxiong
 * @date 2024/5/15/16:04
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanOverviewRSP {

    @ApiModelProperty(value = "本年投放额")
    private ValueUnitDTO loanThisYear;

    @ApiModelProperty(value = "去年同期投放额")
    private ValueUnitDTO loanLastYear;

    @ApiModelProperty(value = "本月新增投放额")
    private ValueUnitDTO loanIncrementThisMonth;

    @ApiModelProperty(value = "本年投放额目标")
    private ValueUnitDTO loanGoalThisYear;

    @ApiModelProperty(value = "本年完成情况")
    private ValueUnitDTO completionPercentThisYear;

    @ApiModelProperty(value = "投放额同比")
    private ValueUnitDTO loanYearOnYearBasis;
}
