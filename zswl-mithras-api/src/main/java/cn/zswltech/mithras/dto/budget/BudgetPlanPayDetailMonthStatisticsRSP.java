package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2025/4/15
 * @description
 */
@Data
public class BudgetPlanPayDetailMonthStatisticsRSP {
    @ApiModelProperty("上月末业务余额")
    private Long endOfLastMonthBalanceTotal;

    @ApiModelProperty("月度新增投放额")
    private Long newActualPayThisMonthTotal;

    @ApiModelProperty("加权平均IRR")
    private Integer priorityAverageIrr;

    @ApiModelProperty("营业收入合计（不含税）")
    private Long incomeWithoutTaxTotal;

    @ApiModelProperty("考核利润合计")
    private Long profitTotal;

    @ApiModelProperty("考核利润合计(扣费后)")
    private Long profitWithoutExpenseTotal;

    @ApiModelProperty("本月末业务余额")
    private Long endOfThisMonthBalanceTotal;
}
