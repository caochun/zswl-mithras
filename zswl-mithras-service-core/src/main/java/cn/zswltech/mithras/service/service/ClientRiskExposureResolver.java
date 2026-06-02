package cn.zswltech.mithras.service.service;

import java.util.List;
import java.util.Map;

/**
 * Resolves client risk exposure for shared business modules.
 */
public interface ClientRiskExposureResolver {

    Map<Long, Long> clientStockRiskExposureMap(List<Long> clientIds);
}
