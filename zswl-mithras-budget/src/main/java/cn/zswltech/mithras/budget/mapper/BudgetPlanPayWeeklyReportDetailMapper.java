package cn.zswltech.mithras.budget.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.zswltech.mithras.budget.mapper.model.BudgetPlanPayWeeklyReportDetail;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

/**
* @description 预算管理-投放计划（月度）-项目周报-详情
* @author vico
* @date 2025-04-11
*/
public interface BudgetPlanPayWeeklyReportDetailMapper extends BaseMapper<BudgetPlanPayWeeklyReportDetail> {

    long calculateTotalNewPay(@Param("budgetPlanPayId") Long budgetPlanPayId,
                              @Param("bizDeptId") Long bizDeptId,
                              @Param("budgetDateFrom") LocalDate budgetDateFrom,
                              @Param("budgetDateTo") LocalDate budgetDateTo);

}