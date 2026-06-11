package cn.zswltech.mithras.budget.mapper.model;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.budget.enums.BudgetPlanDataCategoryEnum;
import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;

/**
 * @author dingqi
 * @date 2025/4/15
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("budget_plan_profit_detail")
public class BudgetPlanProfitDetail extends BaseModelWithLogicDelete {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 预算计划id
     */
    @TableField(value = "budget_plan_id")
    private Long budgetPlanId;

    /**
     * 利润预算id
     */
    @TableField(value = "budget_plan_profit_id")
    private Long budgetPlanProfitId;

    /**
     * 投放计划详情id
     */
    @TableField(value = "budget_plan_pay_detail_id")
    private Long budgetPlanPayDetailId;

    /**
     * 数据周期类型
     */
    @TableField(value = "period_type")
    private String periodType;

    /**
     * 数据周期数值
     */
    @TableField(value = "period_value")
    private String periodValue;

    /**
     * 数据明细分类
     */
    @TableField(value = "data_category")
    private String dataCategory;

    /**
     * 所属部门id
     */
    @TableField(value = "belong_dept_id")
    private Long belongDeptId;

    /**
     * 所属主办id
     */
    @TableField(value = "sponsor_user_id")
    private Long sponsorUserId;

    /**
     * 客户id
     */
    @TableField(value = "client_id")
    private Long clientId;

    /**
     * 客户名称
     */
    @TableField(value = "client_name")
    private String clientName;

    /**
     * 项目评审id
     */
    @TableField(value = "proj_review_id")
    private Long projReviewId;

    /**
     * 合同id
     */
    @TableField(value = "contract_id")
    private Long contractId;

    /**
     * 合同编号
     */
    @TableField(value = "contract_code")
    private String contractCode;

    /**
     * 借据id
     */
    @TableField(value = "receipt_id")
    private Long receiptId;

    /**
     * 借据编号
     */
    @TableField(value = "receipt_code")
    private String receiptCode;

    /**
     * FTP行业分类
     */
    @TableField(value = "ftp_industry_category")
    private String ftpIndustryCategory;

    /**
     * 风控行业分类
     */
    @TableField(value = "risk_control_industry_classify")
    private String riskControlIndustryClassify;

    /**
     * 租赁类型
     */
    @TableField(value = "lease_type")
    private String leaseType;

    /**
     * 租赁期限
     */
    @TableField(value = "term_month")
    private Integer termMonth;

    /**
     * 还款周期
     */
    @TableField(value = "repay_frequency")
    private String repayFrequency;

    /**
     * 项目名称
     */
    @TableField(value = "proj_name")
    private String projName;

    /**
     * 保证金率
     */
    @TableField(value = "deposit_rate")
    private Integer depositRate;

    /**
     * 合同利率
     */
    @TableField(value = "contract_interest_rate")
    private Integer contractInterestRate;

    /**
     * IRR
     */
    @TableField(value = "irr")
    private Integer irr;

    /**
     * XIRR
     */
    @TableField(value = "xirr")
    private Double xirr;

    /**
     * 服务费/咨询费率
     */
    @TableField(value = "consulting_fee_rate")
    private Integer consultingFeeRate;

    /**
     * 服务费/咨询费率（年化）
     */
    @TableField(value = "consulting_fee_rate_year")
    private Integer consultingFeeRateYear;

    /**
     * FTP
     */
    @TableField(value = "ftp")
    private Integer ftp;

    /**
     * 投放额
     */
    @TableField(value = "actual_pay")
    private Long actualPay;

    /**
     * 投放日
     */
    @TableField(value = "pay_date")
    private LocalDate payDate;

    /**
     * 投放日-年份
     */
    @TableField(value = "pay_date_year")
    private Integer payDateYear;

    /**
     * 投放日-月份
     */
    @TableField(value = "pay_date_month")
    private Integer payDateMonth;

    /**
     * 上年末/上月末业务余额
     */
    @TableField(value = "end_of_last_period_balance")
    private Long endOfLastPeriodBalance;

    /**
     * 本年末/本月末业务余额
     */
    @TableField(value = "end_of_this_period_balance")
    private Long endOfThisPeriodBalance;

    /**
     * 全年/全月平均资金占用额
     */
    @TableField(value = "fund_occupy_average")
    private Long fundOccupyAverage;

    /**
     * 利息收入（含税）
     */
    @TableField(value = "interest_income")
    private Long interestIncome;

    /**
     * 利息收入（不含税）
     */
    @TableField(value = "interest_income_without_tax")
    private Long interestIncomeWithoutTax;

    /**
     * 咨询服务费收入（含税）
     */
    @TableField(value = "consulting_fee_income")
    private Long consultingFeeIncome;

    /**
     * 咨询服务费收入（不含税）
     */
    @TableField(value = "consulting_fee_income_without_tax")
    private Long consultingFeeIncomeWithoutTax;

    /**
     * 罚息收入（含税）
     */
    @TableField(value = "penalty_interest_income")
    private Long penaltyInterestIncome;

    /**
     * 罚息收入（不含税）
     */
    @TableField(value = "penalty_interest_income_without_tax")
    private Long penaltyInterestIncomeWithoutTax;

    /**
     * 提前终止补偿金收入（含税）
     */
    @TableField(value = "early_stop_compensation_income")
    private Long earlyStopCompensationIncome;

    /**
     * 提前终止补偿金收入（不含税）
     */
    @TableField(value = "early_stop_compensation_income_without_tax")
    private Long earlyStopCompensationIncomeWithoutTax;

    /**
     * 租前息收入（含税）
     */
    @TableField(value = "before_interest_income")
    private Long beforeInterestIncome;

    /**
     * 租前息收入（不含税）
     */
    @TableField(value = "before_interest_income_without_tax")
    private Long beforeInterestIncomeWithoutTax;

    /**
     * 名义价款收入（含税）
     */
    @TableField(value = "nominal_price_income")
    private Long nominalPriceIncome;

    /**
     * 名义价款收入（不含税）
     */
    @TableField(value = "nominal_price_income_without_tax")
    private Long nominalPriceIncomeWithoutTax;

    /**
     * 营业收入（含税）
     */
    @TableField(value = "income")
    private Long income;

    /**
     * 营业收入（不含税）
     */
    @TableField(value = "income_without_tax")
    private Long incomeWithoutTax;

    /**
     * 营业成本（含税）
     */
    @TableField(value = "cost")
    private Long cost;

    /**
     * 营业成本（不含税）
     */
    @TableField(value = "cost_without_tax")
    private Long costWithoutTax;

    /**
     * 增值税
     */
    @TableField(value = "value_added_tax")
    private Long valueAddedTax;

    /**
     * 印花税
     */
    @TableField(value = "stamp_tax")
    private Long stampTax;

    /**
     * 附加税
     */
    @TableField(value = "additional_tax")
    private Long additionalTax;

    /**
     * 上年末/上月末风险准备金余额
     */
    @TableField(value = "end_of_last_period_risk_fund")
    private Long endOfLastPeriodRiskFund;

    /**
     * 上年末/上月末风险准备金余额（理想值）
     */
    @TableField(value = "end_of_last_period_risk_fund_ideal")
    private Long endOfLastPeriodRiskFundIdeal;

    /**
     * 本年末/本月末风险准备金余额
     */
    @TableField(value = "end_of_this_period_risk_fund")
    private Long endOfThisPeriodRiskFund;

    /**
     * 本年末/本月末风险准备金余额（理想值）
     */
    @TableField(value = "end_of_this_period_risk_fund_ideal")
    private Long endOfThisPeriodRiskFundIdeal;

    /**
     * 累计风险准备金计提/转回
     */
    @TableField(value = "risk_fund_diff")
    private Long riskFundDiff;

    /**
     * 累计风险准备金计提/转回（理想值）
     */
    @TableField(value = "risk_fund_diff_ideal")
    private Long riskFundDiffIdeal;

    /**
     * 毛利
     */
    @TableField(value = "gross_profit")
    private Long grossProfit;

    /**
     * 费用
     */
    @TableField(value = "expense")
    private Long expense;

    /**
     * 费用（理想值）
     */
    @TableField(value = "expense_ideal")
    private Long expenseIdeal;

    /**
     * 利润调整项
     */
    @TableField(value = "profit_adjust")
    private Long profitAdjust;

    /**
     * 备注说明
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 考核利润
     */
    @TableField(value = "assessment_profit")
    private Long assessmentProfit;

    /**
     * 考核利润（理想值）
     */
    @TableField(value = "assessment_profit_ideal")
    private Long assessmentProfitIdeal;

    /**
     * 考核利润（扣费后）
     */
    @TableField(value = "assessment_profit_without_expense")
    private Long assessmentProfitWithoutExpense;

    /**
     * 考核利润（扣费后）（理想值）
     */
    @TableField(value = "assessment_profit_without_expense_ideal")
    private Long assessmentProfitWithoutExpenseIdeal;

    /**
     * 考核利润（原始值）
     */
    @TableField(value = "assessment_profit_original")
    private Long assessmentProfitOriginal;

    /**
     * 考核利润（原始值）（理想值）
     */
    @TableField(value = "assessment_profit_original_ideal")
    private Long assessmentProfitOriginalIdeal;

    /**
     * 考核利润（扣费后）（原始值）
     */
    @TableField(value = "assessment_profit_without_expense_original")
    private Long assessmentProfitWithoutExpenseOriginal;

    /**
     * 考核利润（扣费后）（原始值）（理想值）
     */
    @TableField(value = "assessment_profit_without_expense_original_ideal")
    private Long assessmentProfitWithoutExpenseOriginalIdeal;

    /**
     * 最近一次五级分类结果
     */
    @TableField(value = "asset_classify_result")
    private String assetClassifyResult;

    /**
     * 计算加工数据
     * @param expenseRate 费用比例
     */
    public void calculate(Integer expenseRate) {
        // 风险金计提/转回
        this.riskFundDiff = Optional.ofNullable(this.endOfThisPeriodRiskFund).orElse(0L) - Optional.ofNullable(this.endOfLastPeriodRiskFund).orElse(0L);
        this.riskFundDiffIdeal = Optional.ofNullable(this.endOfThisPeriodRiskFundIdeal).orElse(0L) - Optional.ofNullable(this.endOfLastPeriodRiskFundIdeal).orElse(0L);
        // 考核利润 = 毛利 - 拨备 + 项目利润调整项
        this.assessmentProfit = Optional.ofNullable(this.grossProfit).orElse(0L) - this.riskFundDiff + Optional.ofNullable(this.profitAdjust).orElse(0L);
        this.assessmentProfitIdeal = Optional.ofNullable(this.grossProfit).orElse(0L) - this.riskFundDiffIdeal + Optional.ofNullable(this.profitAdjust).orElse(0L);
        // 扣费后利润
        this.assessmentProfitWithoutExpense = calculateProfitWithoutExpense(BigDecimal.valueOf(this.assessmentProfit), expenseRate).longValue();
        this.assessmentProfitWithoutExpenseIdeal = calculateProfitWithoutExpense(BigDecimal.valueOf(this.assessmentProfitIdeal), expenseRate).longValue();
        // 费用 = 考核利润 - 扣费后利润
        this.expense = this.assessmentProfit - this.assessmentProfitWithoutExpense;
        this.expenseIdeal = this.assessmentProfitIdeal - this.assessmentProfitWithoutExpenseIdeal;
        // 考核利润（原始值） = 毛利 - 拨备
        this.assessmentProfitOriginal = Optional.ofNullable(this.grossProfit).orElse(0L) - this.riskFundDiff;
        this.assessmentProfitOriginalIdeal = Optional.ofNullable(this.grossProfit).orElse(0L) - this.riskFundDiffIdeal;
        // 扣费后利润 = 考核利润（原始值） - 费用
        this.assessmentProfitWithoutExpenseOriginal = this.assessmentProfitOriginal - this.expense;
        this.assessmentProfitWithoutExpenseOriginalIdeal = this.assessmentProfitOriginalIdeal - this.expenseIdeal;
        // 存量考核利润，如果为负数，需要置为0
        if (StrUtil.equals(dataCategory, BudgetPlanDataCategoryEnum.HISTORY.name()) && this.assessmentProfit < 0) {
            this.assessmentProfit = 0L;
        }
        if (StrUtil.equals(dataCategory, BudgetPlanDataCategoryEnum.HISTORY.name()) && this.assessmentProfitWithoutExpense < 0) {
            this.assessmentProfitWithoutExpense = 0L;
        }
        if (StrUtil.equals(dataCategory, BudgetPlanDataCategoryEnum.HISTORY.name()) && this.assessmentProfitIdeal < 0) {
            this.assessmentProfitIdeal = 0L;
        }
        if (StrUtil.equals(dataCategory, BudgetPlanDataCategoryEnum.HISTORY.name()) && this.assessmentProfitWithoutExpenseIdeal < 0) {
            this.assessmentProfitWithoutExpenseIdeal = 0L;
        }
    }

    private BigDecimal calculateProfitWithoutExpense(BigDecimal profit, Integer expenseRate) {
        BigDecimal rate = BigDecimal.valueOf(expenseRate).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP);
        if (profit.longValue() >= 0) {
            return profit.multiply(BigDecimal.ONE.subtract(rate));
        }
        return profit.multiply(BigDecimal.ONE.add(rate));
    }
}
