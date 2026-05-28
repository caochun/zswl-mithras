package cn.zswltech.mithras.service.mapper.budget;

import cn.zswltech.mithras.service.mapper.dto.BudgetPlanCostDetailFundGroupMonthDTO;
import cn.zswltech.mithras.service.mapper.model.budget.BudgetPlanCostDetailFund;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
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
