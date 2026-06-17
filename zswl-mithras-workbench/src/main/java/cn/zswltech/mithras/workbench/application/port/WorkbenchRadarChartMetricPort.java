package cn.zswltech.mithras.workbench.application.port;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

public interface WorkbenchRadarChartMetricPort {
    int countUsersByDeptScope(String deptScope);

    Set<Long> listBusinessUserIds(String roleCode);

    Set<Long> listCurrentYearContractIdsByDeptScope(String deptScope, LocalDateTime startTime,
                                                    Collection<String> contractStatuses);

    Set<Long> listCurrentYearContractIdsBySponsor(Long userId, LocalDateTime startTime,
                                                  Collection<String> contractStatuses);

    BigDecimal calculateLaunchAmountByDeptScope(String deptScope, LocalDate start);

    BigDecimal calculateLaunchAmountBySponsor(Long userId, LocalDate start);

    BigDecimal calculateConsultingFees(Set<Long> contractIds, int personCount);

    BigDecimal calculateCollectionRateByDeptScope(String deptScope, LocalDate start, LocalDate end);

    BigDecimal calculateCollectionRateBySponsor(Long userId, LocalDate start, LocalDate end);

    BigDecimal calculateProfit(Set<Long> contractIds);
}
