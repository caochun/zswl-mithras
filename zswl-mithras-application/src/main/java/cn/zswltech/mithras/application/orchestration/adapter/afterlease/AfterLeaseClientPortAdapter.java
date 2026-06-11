package cn.zswltech.mithras.application.orchestration.adapter.afterlease;

import cn.zswltech.mithras.afterlease.application.AfterLeaseClientPort;
import cn.zswltech.mithras.customer.mapper.model.client.Client;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component
public class AfterLeaseClientPortAdapter implements AfterLeaseClientPort {
    @Resource
    private ClientService clientService;

    @Override
    public Client getById(Long clientId) {
        return clientService.getById(clientId);
    }

    @Override
    public Map<Long, Long> getClientRemainingPrincipalMap(List<Long> clientIds) {
        return clientService.getClientRemainingPrincipalMap(clientIds);
    }

    @Override
    public Map<Long, Long> clientStockRiskExposureMap(List<Long> clientIds) {
        return clientService.clientStockRiskExposureMap(clientIds);
    }
}
