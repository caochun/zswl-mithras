package cn.zswltech.mithras.service.facade.client;

import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Client domain facade.
 * All cross-domain access to client data should go through this interface.
 */
public interface ClientFacade {

    // ========== Client ==========

    Client getClientById(Long id);
    List<Client> listClientByIds(Collection<Long> ids);
    List<Client> listClient(LambdaQueryWrapper<Client> wrapper);
    Map<Long, String> getClientNamesByIds(Collection<Long> ids);

    // ========== CorpCommerceInfo ==========

    CorpCommerceInfo getCorpCommerceByClientId(Long clientId);
    List<CorpCommerceInfo> listCorpCommerce(LambdaQueryWrapper<CorpCommerceInfo> wrapper);
}
