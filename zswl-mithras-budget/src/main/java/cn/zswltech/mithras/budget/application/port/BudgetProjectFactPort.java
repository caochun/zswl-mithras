package cn.zswltech.mithras.budget.application.port;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

public interface BudgetProjectFactPort {

    Map<Long, Long> countEffectiveProjectsByDept(LocalDateTime startTime, LocalDateTime endTime);

    Map<Long, String> mapFtpIndustryCategoryByProjReviewIds(Collection<Long> projReviewIds);
}
