package cn.zswltech.mithras.riskcontrol.application.port;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface RiskControlAssetClassifyPort {

    Optional<Long> currentClassifyId(LocalDate date);

    Set<Long> lastThreeClassifyClientIds(Long assetClassifyId);

    Map<Long, String> latestClassifyResults(Collection<Long> clientIds);
}
