package cn.zswltech.mithras.budget.mapper;

import cn.zswltech.mithras.budget.mapper.dto.BudgetPlanPayDetailIncomeGroupDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.zswltech.mithras.budget.mapper.model.BudgetPlanPayDetailIncomeSharing;
import lombok.Data;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
* @description 预算管理-预算计划-投放计划（非月度）-明细-收入分摊
* @author vico
* @date 2025-04-11
*/
public interface BudgetPlanPayDetailIncomeSharingMapper extends BaseMapper<BudgetPlanPayDetailIncomeSharing> {
    @Select("select " +
                "income_year as year, " +
                "income_month as month, " +
                "sum(ifnull(income,0)) as incomeSum " +
            "from budget_plan_pay_detail_income_sharing " +
            "where budget_plan_pay_detail_id = #{budgetPlanPayDetailId} and deleted = 0 " +
            "group by income_year, income_month")
    List<IncomeGroupDTO> listGroupResult(@Param("budgetPlanPayDetailId") Long budgetPlanPayDetailId);

    long calculateIncomeByDetailId(@Param("detailId") Long detailId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<BudgetPlanPayDetailIncomeGroupDTO> listIncomeGroupByDetail(@Param("budgetPlanPayDetailIds") Collection<Long> budgetPlanPayDetailIds, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Data
    class IncomeGroupDTO {
        private Integer year;
        private Integer month;
        private Long incomeSum;
    }
}