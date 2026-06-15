package cn.zswltech.mithras.foundation.port;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Resolves risk manager users for approval routing.
 */
public interface RiskManagerUserResolver {

    Set<Long> allRiskControlManagerIds();

    Map<Long, List<String>> riskManagerIdsOrderByDeptId();
}
