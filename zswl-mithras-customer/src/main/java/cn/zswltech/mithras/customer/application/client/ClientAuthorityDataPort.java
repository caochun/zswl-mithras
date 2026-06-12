package cn.zswltech.mithras.customer.application.client;

import cn.zswltech.mithras.customer.application.client.model.ClientCopyInfoBO;

public interface ClientAuthorityDataPort {

    void copyFromNewToOld(ClientCopyInfoBO clientCopyInfoBO);

    Long ensureNoProcessViewWhichUserData(Long clientId);
}
