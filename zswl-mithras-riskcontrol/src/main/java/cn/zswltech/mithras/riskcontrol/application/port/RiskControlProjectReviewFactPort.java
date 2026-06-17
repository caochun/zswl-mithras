package cn.zswltech.mithras.riskcontrol.application.port;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface RiskControlProjectReviewFactPort {

    Integer maxReviewPriceMonthCountByClientIds(Set<Long> clientIds, LocalDate snapshotDate);

    Set<Long> newestContractIdsByClientIdsAndRegionalClassifies(Set<Long> clientIds, List<String> regionalClassifies,
                                                                LocalDate snapshotDate);
}
