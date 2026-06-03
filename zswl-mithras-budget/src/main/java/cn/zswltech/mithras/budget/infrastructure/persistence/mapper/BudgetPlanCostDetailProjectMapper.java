package cn.zswltech.mithras.budget.infrastructure.persistence.mapper;

import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.dto.BudgetPlanCostDetailProjectGroupMonthDTO;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.BudgetPlanCostDetailProject;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
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
