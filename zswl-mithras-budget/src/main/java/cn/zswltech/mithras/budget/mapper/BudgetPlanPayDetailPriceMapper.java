package cn.zswltech.mithras.budget.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.zswltech.mithras.budget.mapper.model.BudgetPlanPayDetailPrice;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.Collection;

/**
* @description 预算管理-预算计划-投放计划（非月度）-明细-报价方案
* @author vico
* @date 2025-04-11
*/
public interface BudgetPlanPayDetailPriceMapper extends BaseMapper<BudgetPlanPayDetailPrice> {
    long calculatePrincipalBalance(
            @Param("budgetPlanPayDetailIds") Collection<Long> budgetPlanPayDetailIds,
            @Param("budgetWriteDateFrom") LocalDate budgetWriteDateFrom,
            @Param("budgetDateFrom") LocalDate budgetDateFrom
    );
}