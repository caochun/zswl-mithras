package cn.zswltech.mithras.riskcontrol.application.port;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface RiskControlMarginPort {

    Map<Long, Long> getClientMarginBalances(Collection<Long> clientIds);

    BigDecimal totalDepositByClientIds(Set<Long> clientIds);

    Map<Long, BigDecimal> totalDepositGroupByClientIds(Set<Long> clientIds);

    Map<Long, Long> depositGroupByClientId(Set<Long> clientIds);

    Long sumReportMarginAmount(Collection<Long> contractIds);
}
