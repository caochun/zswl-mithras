package cn.zswltech.mithras.kpi.mapper.model;

import cn.zswltech.mithras.common.enums.ProjectBizType;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 绩效考核-项目利润明细-记录表
 * @author vico
 * @date 2024-09-25
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class KpiFinanceProjectProfitRecord extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * finance_project_profit_detail id
    */
    @TableField("profit_detail_id")
    private Long profitDetailId;

    /**
    * 批次号
    */
    @TableField("batch_number")
    private int batchNumber;

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

    @TableField("receipt_id")
    private Long receiptId;

    /**
     * 借据首次投放日期
     **/
    @TableField("receipt_first_payment_date")
    private LocalDate receiptFirstPaymentDate;

    /**
     * 项目申报授信金额
     */
    @TableField("apply_credit_amount")
    private Long applyCreditAmount;

    /**
     * {@link ProjectBizType#name()}
     **/
    @TableField("biz_type")
    private String bizType;

    /**
     * 租赁类型。直租、回租、经营性租赁
     * {@link LeaseType#name()}
     */
    @TableField("lease_type")
    private String leaseType;

    /**
    * 投放日
    */
    @TableField("contract_start_date")
    private LocalDate contractStartDate;

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
    * 当月风险金
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
    * 当年累计风险金
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
