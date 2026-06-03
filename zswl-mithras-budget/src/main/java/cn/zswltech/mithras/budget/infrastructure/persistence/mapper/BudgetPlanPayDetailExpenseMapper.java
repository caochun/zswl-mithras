package cn.zswltech.mithras.budget.infrastructure.persistence.mapper;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.dto.BudgetPlanPayDetailExpenseGroupDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.BudgetPlanPayDetailExpense;
import lombok.Data;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
* @description 预算管理-预算计划-投放计划（非月度）-明细-期间费用
* @author vico
* @date 2025-04-11
*/
public interface BudgetPlanPayDetailExpenseMapper extends BaseMapper<BudgetPlanPayDetailExpense> {
    @Select("select " +
                "expense_date_year as expenseDateYear, " +
                "sum(ifnull(value_added_tax,0)) as valueAddedTaxYearTotal, " +
                "sum(ifnull(stamp_tax,0)) as stampTaxYearTotal, " +
                "sum(ifnull(additional_tax,0)) as additionalTaxYearTotal, " +
                "sum(ifnull(gross_profit,0)) as grossProfitYearTotal, " +
                "sum(ifnull(expense,0)) as expenseYearTotal, " +
                "sum(ifnull(risk_fund_diff,0)) as riskFundDiffYearTotal, " +
                "sum(ifnull(assessment_profit,0)) as profitYearTotal " +
            "from budget_plan_pay_detail_expense " +
            "where budget_plan_pay_detail_id = #{budgetPlanPayDetailId} and deleted = 0 " +
            "group by expense_date_year")
    List<BudgetPlanPayDetailExpenseMapper.IncomeGroupDTO> listGroupResult(@Param("budgetPlanPayDetailId") Long budgetPlanPayDetailId);

    @Select("select expense_date_year, expense_date_month, risk_fund from budget_plan_pay_detail_expense where budget_plan_pay_detail_id = #{budgetPlanPayDetailId} and expense_date_year = #{expenseDateYear} and deleted = 0 ")
    List<BudgetPlanPayDetailExpense> listByDateYear(@Param("budgetPlanPayDetailId") Long budgetPlanPayDetailId, @Param("expenseDateYear") int expenseDateYear);

    @Select("select ifnull(sum(ifnull(stamp_tax, 0)), 0) from budget_plan_pay_detail_expense where budget_plan_pay_detail_id = #{budgetPlanPayDetailId} and deleted = 0")
    long calculateStamp(@Param("budgetPlanPayDetailId") Long budgetPlanPayDetailId);

    List<BudgetPlanPayDetailExpenseGroupDTO> listExpenseGroupByDetail(@Param("budgetPlanPayDetailIds") Collection<Long> budgetPlanPayDetailIds, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Data
    class IncomeGroupDTO {
        private Integer expenseDateYear;
        private Long valueAddedTaxYearTotal;
        private Long stampTaxYearTotal;
        private Long additionalTaxYearTotal;
        private Long grossProfitYearTotal;
        private Long expenseYearTotal;
        private Long profitYearTotal;
        private Long riskFundDiffYearTotal;
    }
}