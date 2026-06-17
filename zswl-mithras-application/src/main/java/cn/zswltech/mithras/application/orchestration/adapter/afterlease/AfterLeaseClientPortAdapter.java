package cn.zswltech.mithras.application.orchestration.adapter.afterlease;

import cn.zswltech.mithras.afterlease.application.AfterLeaseClientPort;
import cn.zswltech.mithras.afterlease.application.AfterLeaseClientSnapshot;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AfterLeaseClientPortAdapter implements AfterLeaseClientPort {
    @Resource
    private ClientService clientService;

    @Override
    public AfterLeaseClientSnapshot getById(Long clientId) {
        return toSnapshot(clientService.getById(clientId));
    }

    @Override
    public Map<Long, AfterLeaseClientSnapshot> listByIds(Collection<Long> clientIds) {
        if (clientIds == null || clientIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return clientService.listByIds(clientIds)
                .stream()
                .map(this::toSnapshot)
                .collect(Collectors.toMap(AfterLeaseClientSnapshot::getClientId, item -> item));
    }

    @Override
    public Map<Long, Long> getClientRemainingPrincipalMap(List<Long> clientIds) {
        return clientService.getClientRemainingPrincipalMap(clientIds);
    }

    @Override
    public Map<Long, Long> clientStockRiskExposureMap(List<Long> clientIds) {
        return clientService.clientStockRiskExposureMap(clientIds);
    }

    private AfterLeaseClientSnapshot toSnapshot(Client client) {
        if (client == null) {
            return null;
        }
        AfterLeaseClientSnapshot snapshot = new AfterLeaseClientSnapshot();
        snapshot.setClientId(client.getId());
        snapshot.setClientName(client.getClientName());
        snapshot.setClientType(client.getClientType());
        snapshot.setBelongSponsorId(client.getBelongSponsorId());
        snapshot.setBelongDeptId(client.getBelongDeptId());
        return snapshot;
    }
}
