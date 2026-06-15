package cn.zswltech.mithras.kpi.application.distribution.port;

import java.util.Collection;
import java.util.Map;

public interface KpiProjectDistributionContractPort {

    Map<String, Long> mapEffectiveContractIdsByCodes(Collection<String> contractCodes);
}
