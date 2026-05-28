package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2025/5/22
 * @description
 */
@Data
public class BudgetPlanProfitDetailModifyREQ {
    @NotNull(message = "行数据id不能为空")
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("上年末/上月末业务余额")
    private Long endOfLastPeriodBalance;

    @ApiModelProperty("本年末/本月末业务余额")
    private Long endOfThisPeriodBalance;

    @ApiModelProperty("全年/全月平均资金占用额")
    private Long fundOccupyAverage;

    @ApiModelProperty("利息收入（含税）")
    private Long interestIncome;

    @ApiModelProperty("咨询服务费收入（含税）")
    private Long consultingFeeIncome;

    @ApiModelProperty("营业收入（含税）")
    private Long income;

    @ApiModelProperty("营业收入（不含税）")
    private Long incomeWithoutTax;

    @ApiModelProperty("营业成本（含税）")
    private Long cost;

    @ApiModelProperty("营业成本（不含税）")
    private Long costWithoutTax;

    @ApiModelProperty("增值税")
    private Long valueAddedTax;

    @ApiModelProperty("印花税")
    private Long stampTax;

    @ApiModelProperty("附加税")
    private Long additionalTax;

    @ApiModelProperty("上年末/上月末风险准备金余额")
    private Long endOfLastPeriodRiskFund;

    @ApiModelProperty("本年末/本月末风险准备金余额")
    private Long endOfThisPeriodRiskFund;

    @ApiModelProperty("累计风险准备金计提/转回")
    private Long riskFundDiff;

    @ApiModelProperty("毛利")
    private Long grossProfit;

    @ApiModelProperty("费用")
    private Long expense;

    @ApiModelProperty("利润调整项")
    private Long profitAdjust;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("考核利润")
    private Long assessmentProfit;

    @ApiModelProperty("考核利润（扣费后）")
    private Long assessmentProfitWithoutExpense;

    @ApiModelProperty("考核利润（原始值）")
    private Long assessmentProfitOriginal;

    @ApiModelProperty("考核利润（扣费后）（原始值）")
    private Long assessmentProfitWithoutExpenseOriginal;
}
