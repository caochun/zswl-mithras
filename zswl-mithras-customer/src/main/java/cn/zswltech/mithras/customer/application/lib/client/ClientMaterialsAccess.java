package cn.zswltech.mithras.customer.application.lib.client;

import cn.zswltech.mithras.customer.model.client.Client;

public interface ClientMaterialsAccess {

    Client getClient(Long clientId);

    boolean isIntraGroupCollaboration(Long clientId);
}
