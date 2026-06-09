package cn.zswltech.mithras.customer.application.client;

import cn.zswltech.mithras.customer.domain.enums.client.ClientProcessStatus;
import cn.zswltech.mithras.customer.domain.enums.client.ClientStatus;

public interface ClientDataSaveCheckPort {
    boolean canSave(Long clientId);

    boolean hasRelatedProcess(Long clientId);

    void recordClientStatus(Long clientId, ClientStatus clientStatus, ClientProcessStatus processStatus);
}
