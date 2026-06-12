package cn.zswltech.mithras.application.orchestration.adapter.client;

import cn.zswltech.mithras.customer.application.client.model.ClientCopyInfoBO;
import cn.zswltech.mithras.customer.application.client.ClientAuthorityDataPort;
import cn.zswltech.mithras.application.orchestration.client.authority.ClientAuthorityUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ClientAuthorityDataPortAdapter implements ClientAuthorityDataPort {

    @Resource
    private ClientAuthorityUtil clientAuthorityUtil;

    @Override
    public void copyFromNewToOld(ClientCopyInfoBO clientCopyInfoBO) {
        clientAuthorityUtil.copyFromNewToOld(clientCopyInfoBO);
    }

    @Override
    public Long ensureNoProcessViewWhichUserData(Long clientId) {
        return clientAuthorityUtil.ensureNoProcessViewWhichUserData(clientId);
    }
}
