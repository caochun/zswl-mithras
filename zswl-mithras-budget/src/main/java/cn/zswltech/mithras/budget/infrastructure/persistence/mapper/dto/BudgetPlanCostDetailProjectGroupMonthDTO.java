package cn.zswltech.mithras.budget.infrastructure.persistence.mapper.dto;

import lombok.Data;

/**
 * @author dingqi
 * @date 2025/6/26
 * @description
 */
@Data
public class BudgetPlanCostDetailProjectGroupMonthDTO {
    private Integer year;
    private Integer month;
    private Long rentSum;
    private Long principalSum;
    private Long interestSum;
    private Long depositSum;
    private Long consultingFeeSum;
    private Long payAmountSum;
}
