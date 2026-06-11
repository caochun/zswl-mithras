package cn.zswltech.mithras.finance.mapper.model.finance;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;

/**
 * @description 绩效考核-项目利润明细（按借据）
 * @author vico
 * @date 2024-10-25
 */
@Data
public class FinanceProjectProfitDetailReceipt extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 项目利润记录id
    */
    @TableField("project_profit_id")
    private Long projectProfitId;

    /**
    * 年份
    */
    @TableField("year")
    private Integer year;

    /**
    * 月份
    */
    @TableField("month")
    private Integer month;

    /**
    * 合同id
    */
    @TableField("contract_id")
    private Long contractId;

    /**
    * 借据id
    */
    @TableField("receipt_id")
    private Long receiptId;

    /**
    * 借据投放日
    */
    @TableField("receipt_start_date")
    private LocalDate receiptStartDate;

    /**
    * 当月收入
    */
    @TableField("income_this_month")
    private Long incomeThisMonth;

    /**
    * 当月资金成本
    */
    @TableField("cost_this_month")
    private Long costThisMonth;

    /**
    * 本月风险金计提/冲抵（原：当月风险金）
    */
    @TableField("risk_this_month")
    private Long riskThisMonth;

    /**
    * 当月税金及附加
    */
    @TableField("tax_this_month")
    private Long taxThisMonth;

    /**
    * 当月项目利润
    */
    @TableField("profit_this_month")
    private Long profitThisMonth;

    /**
    * 当年累计收入
    */
    @TableField("total_income_this_year")
    private Long totalIncomeThisYear;

    /**
    * 当年累计资金成本
    */
    @TableField("total_cost_this_year")
    private Long totalCostThisYear;

    /**
    * 本月风险金余额（原：当年累计风险金）
    */
    @TableField("total_risk_this_year")
    private Long totalRiskThisYear;

    /**
    * 当年累计税金及附加
    */
    @TableField("total_tax_this_year")
    private Long totalTaxThisYear;

    /**
    * 当年累计利润总额
    */
    @TableField("total_profit_this_year")
    private Long totalProfitThisYear;

    /**
    * 费用计提比例快照
    */
    @TableField("expense_radio")
    private Integer expenseRadio;

    /**
    * 当月附加税
    */
    @TableField("additional_tax_this_month")
    private Long additionalTaxThisMonth;

    /**
    * 当月印花税
    */
    @TableField("stamp_tax_this_month")
    private Long stampTaxThisMonth;

    /**
    * 当年累计附加税
    */
    @TableField("total_additional_tax_this_year")
    private Long totalAdditionalTaxThisYear;

    /**
    * 当年累计印花税
    */
    @TableField("total_stamp_tax_this_year")
    private Long totalStampTaxThisYear;

    /**
    * 本年累计毛利
    */
    @TableField("total_gross_profit_this_year")
    private Long totalGrossProfitThisYear;

    /**
    * 本月毛利
    */
    @TableField("gross_profit_this_month")
    private Long grossProfitThisMonth;

    /**
    * 本月收入
    */
    @TableField("revenue_this_month")
    private Long revenueThisMonth;

    /**
    * 考核部门id
    */
    @TableField("assess_dept_id")
    private Long assessDeptId;

    /**
    * 本年累计利润总额扣费前
    */
    @TableField("total_profit_this_year_before")
    private Long totalProfitThisYearBefore;

    /**
    * 年初风险金余额
    */
    @TableField("risk_balance_begin_year")
    private Long riskBalanceBeginYear;

    /**
    * 累计风险金计提/冲抵
    */
    @TableField("total_risk_balance_this_year")
    private Long totalRiskBalanceThisYear;

}
