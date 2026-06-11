package cn.zswltech.mithras.budget.mapper;

import cn.zswltech.mithras.budget.mapper.dto.BudgetPlanCostDetailFundGroupMonthDTO;
import cn.zswltech.mithras.budget.mapper.model.BudgetPlanCostDetailFund;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dingqi
 * @date 2025/6/25
 * @description
 */
public interface BudgetPlanCostDetailFundMapper extends CustomBaseMapper<BudgetPlanCostDetailFund> {
    List<BudgetPlanCostDetailFundGroupMonthDTO> selectGroupByYearMonth(@Param("budgetPlanCostId") Long budgetPlanCostId);
}
