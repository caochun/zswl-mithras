package cn.zswltech.mithras.application.adapter.client;

import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.customer.application.client.ClientDataSaveCheckPort;
import cn.zswltech.mithras.customer.domain.enums.client.ClientProcessStatus;
import cn.zswltech.mithras.customer.domain.enums.client.ClientStatus;
import cn.zswltech.mithras.service.service.client.ClientService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ClientDataSaveCheckPortAdapter implements ClientDataSaveCheckPort {
    @Resource
    private ClientService clientService;

    @Override
    public boolean canSave(Long clientId) {
        return clientService.canSave(clientId);
    }

    @Override
    public boolean hasRelatedProcess(Long clientId) {
        ProcessResp processResp = clientService.findRelatedProcess(clientId);
        return processResp != null;
    }

    @Override
    public void recordClientStatus(Long clientId, ClientStatus clientStatus, ClientProcessStatus processStatus) {
        clientService.recordClientStatus(clientId, clientStatus, processStatus);
    }
}
