package cn.zswltech.mithras.budget.application.port;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Collection facts required by budget calculations.
 */
public interface BudgetCollectionFactPort {

    Map<Long, Boolean> getContractPromotion(List<Long> contractIds, Integer interval);

    Map<Long, Long> calculateRemainingPrincipalGroupByDeptId(LocalDate targetDate);
}
