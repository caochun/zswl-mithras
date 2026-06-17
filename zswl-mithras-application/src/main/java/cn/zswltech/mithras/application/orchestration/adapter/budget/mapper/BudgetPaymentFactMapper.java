package cn.zswltech.mithras.application.orchestration.adapter.budget.mapper;

import cn.zswltech.mithras.budget.application.port.BudgetContractPaymentFactSnapshot;
import cn.zswltech.mithras.budget.application.port.BudgetPaymentActualSnapshot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface BudgetPaymentFactMapper {

    List<BudgetContractPaymentFactSnapshot> listContractPayInfoBetween(@Param("startDate") LocalDate startDate,
                                                                       @Param("endDate") LocalDate endDate);

    List<BudgetContractPaymentFactSnapshot> listContractPayInfoBeforeTargetDate(@Param("targetDate") LocalDate targetDate);

    List<BudgetPaymentActualSnapshot> listPaymentActualBetween(@Param("startDate") LocalDate startDate,
                                                               @Param("endDate") LocalDate endDate);
}
