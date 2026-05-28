package cn.zswltech.mithras.dto.dashboard.boss;

import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yangxiong
 * @date 2024/5/15/15:56
 * @description
 */
@NoArgsConstructor
@Data
public class BalanceOverviewRSP {

    @ApiModelProperty(value = "资产余额")
    private ValueUnitDTO assetsBalance;

    @ApiModelProperty(value = "上年同期资产余额")
    private ValueUnitDTO assetsBalanceLastYear;

    @ApiModelProperty(value = "资产余额本月新增")
    private ValueUnitDTO assetsBalanceIncrementThisMonth;

    @ApiModelProperty(value = "资产余额同比")
    private ValueUnitDTO assetsBalanceYearOnYearBasis;

    @ApiModelProperty(value = "资产余额本年目标")
    private ValueUnitDTO assetsBalanceGoalThisYear;

    @ApiModelProperty(value = "本年完成情况")
    private ValueUnitDTO completionPercentThisYear;

}
