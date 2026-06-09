package cn.zswltech.mithras.application.adapter.customer;

import cn.zswltech.mithras.customer.application.lib.client.ClientMaterialsAccess;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.util.ClientAuthorityUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ClientMaterialsAccessAdapter implements ClientMaterialsAccess {

    @Resource
    private ClientService clientService;
    @Resource
    private ClientAuthorityUtil clientAuthorityUtil;

    @Override
    public Client getClient(Long clientId) {
        return clientService.getById(clientId);
    }

    @Override
    public boolean isIntraGroupCollaboration(Long clientId) {
        return clientAuthorityUtil.isIntraGroupCollaboration(clientId);
    }
}
