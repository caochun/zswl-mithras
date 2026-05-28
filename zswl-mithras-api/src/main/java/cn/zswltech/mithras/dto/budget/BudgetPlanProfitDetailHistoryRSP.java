package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2025/5/14
 * @description
 */
@Data
public class BudgetPlanProfitDetailHistoryRSP {
    @ApiModelProperty("部门id")
    private Long belongDeptId;

    @ApiModelProperty("部门名称")
    private String belongDeptName;

    @ApiModelProperty("上月末/上年末业务余额")
    private Long lastPeriodBalance = 0L;

    @ApiModelProperty("营业收入（不含税）")
    private Long incomeWithoutTax = 0L;

    @ApiModelProperty("营业成本（不含税）")
    private Long costWithoutTax = 0L;

    @ApiModelProperty("增值税")
    private Long valueAddedTax = 0L;

    @ApiModelProperty("税金及附加")
    private Long taxOther = 0L;

    @ApiModelProperty("当月/当年平均资金占用")
    private Long averageOccupyThisPeriod = 0L;

    @ApiModelProperty("风险准备金")
    private Long riskFund = 0L;

    @ApiModelProperty("考核利润")
    private Long profit = 0L;

    @ApiModelProperty("本月末/本年末资产余额")
    private Long thisPeriodBalance = 0L;
}
