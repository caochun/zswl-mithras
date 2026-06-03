package cn.zswltech.mithras.budget.infrastructure.persistence.mapper.dto;

import lombok.Data;

/**
 * @author dingqi
 * @date 2025/5/8
 * @description
 */
@Data
public class BudgetPlanPayDetailExpenseGroupDTO {
    private Long budgetPlanPayDetailId;
    private Long taxSum;
    private Long riskFundDiffSum;
    private Long assessmentProfitSum;
    private Long expenseSum;
}
