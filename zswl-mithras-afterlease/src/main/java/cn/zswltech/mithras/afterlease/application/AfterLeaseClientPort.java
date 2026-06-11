package cn.zswltech.mithras.afterlease.application;

import cn.zswltech.mithras.customer.mapper.model.client.Client;

import java.util.List;
import java.util.Map;

public interface AfterLeaseClientPort {
    Client getById(Long clientId);

    Map<Long, Long> getClientRemainingPrincipalMap(List<Long> clientIds);

    Map<Long, Long> clientStockRiskExposureMap(List<Long> clientIds);
}
