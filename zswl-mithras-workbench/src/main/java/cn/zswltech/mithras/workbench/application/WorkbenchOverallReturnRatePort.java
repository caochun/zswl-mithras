package cn.zswltech.mithras.workbench.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public interface WorkbenchOverallReturnRatePort {
    Set<Long> listActiveClientIdsByDeptCode(String deptCode);

    Set<Long> listClientIdsByIndustry(String industry);

    Set<Long> listClientIdsExcludingIndustries(Set<String> industries);

    BigDecimal calculateAverageIrr(Set<Long> clientIds, LocalDate endTime);
}
