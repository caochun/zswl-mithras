package cn.zswltech.mithras.service.mapper.model.finance;

import cn.zswltech.mithras.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2023/6/16
 * @description 财务管理-项目利润明细
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("finance_project_profit_detail")
public class FinanceProjectProfitDetail extends BaseModel {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 项目利润记录id
     */
    @TableField(value = "project_profit_id")
    private Long projectProfitId;

    /**
     * 年份
     */
    @TableField(value = "year")
    private Integer year;

    /**
     * 月份
     */
    @TableField(value = "month")
    private Integer month;

    /**
     * 合同id
     */
    @TableField(value = "contract_id")
    private Long contractId;

    /**
     * 投放日
     */
    @TableField(value = "contract_start_date")
    private LocalDate contractStartDate;

    /**
     * 当月利息收入
     */
    @TableField(value = "income_this_month")
    private Long incomeThisMonth;

    /**
     * 当月资金成本
     */
    @TableField(value = "cost_this_month")
    private Long costThisMonth;

    /**
     * 本月风险金计提/冲抵（原：当月风险金）
     */
    @TableField(value = "risk_this_month")
    private Long riskThisMonth;

    /**
     * 当月税金及附加
     * @deprecated 拆分为附加税和印花税
     */
    @Deprecated
    @TableField(value = "tax_this_month")
    private Long taxThisMonth;

    /**
     * 当月利润
     */
    @TableField(value = "profit_this_month")
    private Long profitThisMonth;

    /**
     * 当年累计利息收入
     */
    @TableField(value = "total_income_this_year")
    private Long totalIncomeThisYear;

    /**
     * 当年累计资金成本
     */
    @TableField(value = "total_cost_this_year")
    private Long totalCostThisYear;

    /**
     * 本月风险金余额（原：当年累计风险金）
     */
    @TableField(value = "total_risk_this_year")
    private Long totalRiskThisYear;

    /**
     * 当年累计税金及附加
     * @deprecated 拆分为附加税和印花税
     */
    @Deprecated
    @TableField(value = "total_tax_this_year")
    private Long totalTaxThisYear;

    /**
     * 当年累计附加税
     */
    @TableField(value = "total_additional_tax_this_year")
    private Long totalAdditionalTaxThisYear;

    /**
     * 当年累计印花税
     */
    @TableField(value = "total_stamp_tax_this_year")
    private Long totalStampTaxThisYear;

    /**
     * 当年累计利润总额
     */
    @TableField(value = "total_profit_this_year")
    private Long totalProfitThisYear;

    /**
     * 费用计提比例快照
     */
    @TableField(value = "expense_radio")
    private Integer expenseRadio;

    /**
     * 本年累计毛利
     */
    @TableField(value = "total_gross_profit_this_year")
    private Long totalGrossProfitThisYear;

    /**
     * 本月毛利
     */
    @TableField(value = "gross_profit_this_month")
    private Long grossProfitThisMonth;

    /**
     * 本月收入
     */
    @TableField(value = "revenue_this_month")
    private Long revenueThisMonth;

    /**
     * 考核部门
     */
    @TableField(value = "assess_dept_id")
    private Long assessDeptId;

    /**
     * 本年累计利润总额(扣费前)
     */
    @TableField(value = "total_profit_this_year_before")
    private Long totalProfitThisYearBefore;

    /**
     * 年初风险金余额
     */
    @TableField("risk_balance_begin_year")
    private Long riskBalanceBeginYear;

    /**
     * 累计风险金计提/冲抵
     */
    @TableField(value = "total_risk_balance_this_year")
    private Long totalRiskBalanceThisYear;
}
