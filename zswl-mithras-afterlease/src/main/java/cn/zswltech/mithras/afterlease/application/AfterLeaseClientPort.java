package cn.zswltech.mithras.afterlease.application;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface AfterLeaseClientPort {
    AfterLeaseClientSnapshot getById(Long clientId);

    Map<Long, AfterLeaseClientSnapshot> listByIds(Collection<Long> clientIds);

    Map<Long, Long> getClientRemainingPrincipalMap(List<Long> clientIds);

    Map<Long, Long> clientStockRiskExposureMap(List<Long> clientIds);
}
