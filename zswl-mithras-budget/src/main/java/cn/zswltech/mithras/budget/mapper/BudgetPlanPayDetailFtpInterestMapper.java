package cn.zswltech.mithras.budget.mapper;
import cn.zswltech.mithras.budget.mapper.dto.BudgetPlanPayDetailCostGroupDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.zswltech.mithras.budget.mapper.model.BudgetPlanPayDetailFtpInterest;
import lombok.Data;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
* @description 预算管理-预算计划-投放计划（非月度）-明细-资金成本（ftp计息）
* @author vico
* @date 2025-04-11
*/
public interface BudgetPlanPayDetailFtpInterestMapper extends BaseMapper<BudgetPlanPayDetailFtpInterest> {
    @Select("select " +
                "interest_year as interestYear, " +
                "interest_month as interestMonth, " +
                "sum(ifnull(cash_interest,0)) as costMonthTotal, " +
                "sum(ifnull(cash_occupy,0)) as occupyMonthTotal " +
            "from budget_plan_pay_detail_ftp_interest " +
            "where budget_plan_pay_detail_id = #{budgetPlanPayDetailId} and deleted = 0 " +
            "group by interest_year, interest_month")
    List<BudgetPlanPayDetailFtpInterestMapper.IncomeGroupDTO> listGroupResult(@Param("budgetPlanPayDetailId") Long budgetPlanPayDetailId);

    @Select("select " +
            "ifnull(sum(if(cash_occupy > 0, cash_occupy, 0)), 0) " +
            "from budget_plan_pay_detail_ftp_interest " +
            "where budget_plan_pay_detail_id = #{detailId} and deleted = 0 and interest_date >= #{startDate} and interest_date <= #{endDate}")
    long calculateCashOccupy(@Param("detailId") Long detailId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    long calculateCostByDetailId(@Param("detailId") Long detailId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<BudgetPlanPayDetailCostGroupDTO> listCostGroupByDetail(@Param("budgetPlanPayDetailIds") Collection<Long> budgetPlanPayDetailIds, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Data
    class IncomeGroupDTO {
        private Integer interestYear;
        private Integer interestMonth;
        private Long costMonthTotal;
        private Long occupyMonthTotal;
    }
}