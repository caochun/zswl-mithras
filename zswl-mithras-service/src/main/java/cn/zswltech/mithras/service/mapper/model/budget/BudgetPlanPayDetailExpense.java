package cn.zswltech.mithras.service.mapper.model.budget;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 预算管理-预算计划-投放计划（非月度）-明细-期间费用
 * @author vico
 * @date 2025-04-11
 */
@Data
public class BudgetPlanPayDetailExpense extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 逻辑删除，0-未删除
    */
    @TableField("deleted")
    private Integer deleted;

    /**
    * 预算计划id
    */
    @TableField("budget_plan_id")
    private Long budgetPlanId;

    /**
    * 投放计划id
    */
    @TableField("budget_plan_pay_id")
    private Long budgetPlanPayId;

    /**
    * 投放计划-明细id
    */
    @TableField("budget_plan_pay_detail_id")
    private Long budgetPlanPayDetailId;

    /**
    * 费用日期
    */
    @TableField("expense_date")
    private LocalDate expenseDate;

    /**
    * 费用日期-年份
    */
    @TableField("expense_date_year")
    private Integer expenseDateYear;

    /**
    * 费用日期-月份
    */
    @TableField("expense_date_month")
    private Integer expenseDateMonth;

    /**
    * 增值税
    */
    @TableField("value_added_tax")
    private Long valueAddedTax;

    /**
    * 印花税
    */
    @TableField("stamp_tax")
    private Long stampTax;

    /**
    * 附加税
    */
    @TableField("additional_tax")
    private Long additionalTax;

    /**
    * 风险准备金（拨备）
    */
    @TableField("risk_fund")
    private Long riskFund;

    /**
     * 风险准备金计提/转回
     */
    @TableField("risk_fund_diff")
    private Long riskFundDiff;

    /**
    * 毛利润
    */
    @TableField("gross_profit")
    private Long grossProfit;

    /**
    * 费用
    */
    @TableField("expense")
    private Long expense;

    /**
    * 考核利润
    */
    @TableField("assessment_profit")
    private Long assessmentProfit;

    @Override
    public void reset() {
        super.reset();
        this.id = null;
    }
}
