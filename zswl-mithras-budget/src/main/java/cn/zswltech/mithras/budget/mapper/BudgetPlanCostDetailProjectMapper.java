package cn.zswltech.mithras.budget.mapper;

import cn.zswltech.mithras.budget.mapper.dto.BudgetPlanCostDetailProjectGroupMonthDTO;
import cn.zswltech.mithras.budget.mapper.model.BudgetPlanCostDetailProject;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dingqi
 * @date 2025/6/25
 * @description
 */
public interface BudgetPlanCostDetailProjectMapper extends CustomBaseMapper<BudgetPlanCostDetailProject> {
    List<BudgetPlanCostDetailProjectGroupMonthDTO> selectGroupByYearMonth(@Param("budgetPlanCostId") Long budgetPlanCostId, @Param("dataCategory") String dataCategory);
}
