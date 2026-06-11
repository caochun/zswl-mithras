package cn.zswltech.mithras.foundation.port;

import java.util.List;
import java.util.Map;

/**
 * Resolves client risk exposure for shared business modules.
 */
public interface ClientRiskExposureResolver {

    Map<Long, Long> clientStockRiskExposureMap(List<Long> clientIds);
}
