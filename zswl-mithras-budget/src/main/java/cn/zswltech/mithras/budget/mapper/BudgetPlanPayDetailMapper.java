package cn.zswltech.mithras.budget.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.zswltech.mithras.budget.mapper.model.BudgetPlanPayDetail;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.Collection;

/**
* @description 预算管理-预算计划-投放计划-明细
* @author vico
* @date 2025-04-11
*/
public interface BudgetPlanPayDetailMapper extends BaseMapper<BudgetPlanPayDetail> {
    long calculatePrincipalBalance(
            @Param("budgetPlanDetailIds") Collection<Long> budgetPlanDetailIds,
            @Param("budgetWriteDateFrom") LocalDate budgetWriteDateFrom,
            @Param("budgetDateFrom") LocalDate budgetDateFrom
    );
}