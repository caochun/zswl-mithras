package cn.zswltech.mithras.service.mapper.budget;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.zswltech.mithras.service.mapper.model.budget.BudgetPlanPayDetailCashFlow;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.Collection;

/**
* @description 预算管理-预算计划-投放计划（非月度）-明细-现金流计划
* @author vico
* @date 2025-04-11
*/
public interface BudgetPlanPayDetailCashFlowMapper extends BaseMapper<BudgetPlanPayDetailCashFlow> {
    long calculatePrincipalBalance(@Param("budgetPlanPayDetailIds") Collection<Long> budgetPlanPayDetailIds, @Param("targetDate") LocalDate targetDate, @Param("payDate") LocalDate payDate);
}