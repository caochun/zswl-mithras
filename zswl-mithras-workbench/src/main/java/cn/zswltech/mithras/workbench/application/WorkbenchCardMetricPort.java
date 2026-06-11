package cn.zswltech.mithras.workbench.application;

import cn.zswltech.mithras.workbench.enums.WorkbenchMetricTimeScope;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface WorkbenchCardMetricPort {
    List<String> getCurrentUserRoles();

    String countFirstLaunchClientsBySponsor(Long userId, LocalDateTime startTime);

    String countProjectEstablishBySponsor(Long userId, LocalDateTime startTime);

    WorkbenchCardReviewStats reviewStatsBySponsor(Long userId, LocalDateTime startTime);

    String calculateLaunchAmountBySponsor(Long userId, LocalDate startDay);

    String calculateRemainingPrincipalBySponsor(Long userId);

    String countInventoryProjectsBySponsor(Long userId);

    String countOverdueProjectsBySponsor(Long userId);

    String countDefectiveProjectsBySponsor(Long userId);

    Map<String, String> countFirstLaunchClientsByDeptScope();

    Map<String, String> countProjectEstablishByDeptScope(WorkbenchMetricTimeScope timeScope);

    Map<String, WorkbenchCardReviewStats> reviewStatsByDeptScope();

    Map<String, String> calculateLaunchAmountByDeptScope(WorkbenchMetricTimeScope timeScope);

    Map<String, String> calculateRemainingPrincipalByDeptScope();

    Map<String, String> countInventoryProjectsByDeptScope();

    Map<String, String> countOverdueProjectsByDeptScope();

    Map<String, String> countDefectiveProjectsByDeptScope();
}
