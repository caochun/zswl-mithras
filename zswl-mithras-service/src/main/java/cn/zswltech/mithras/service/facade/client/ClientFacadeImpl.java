package cn.zswltech.mithras.service.facade.client;

import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.client.CorpCommerceInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClientFacadeImpl implements ClientFacade {

    @Resource
    private ClientService clientService;
    @Resource
    private CorpCommerceInfoService corpCommerceInfoService;

    @Override
    public Client getClientById(Long id) {
        return clientService.getById(id);
    }

    @Override
    public List<Client> listClientByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return clientService.listByIds(ids);
    }

    @Override
    public List<Client> listClient(LambdaQueryWrapper<Client> wrapper) {
        return clientService.list(wrapper);
    }

    @Override
    public Map<Long, String> getClientNamesByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyMap();
        List<Client> clients = clientService.listByIds(ids);
        return clients.stream().collect(Collectors.toMap(Client::getId, Client::getClientName, (a, b) -> a));
    }

    @Override
    public CorpCommerceInfo getCorpCommerceByClientId(Long clientId) {
        return corpCommerceInfoService.getOne(
                Wrappers.<CorpCommerceInfo>lambdaQuery().eq(CorpCommerceInfo::getClientId, clientId));
    }

    @Override
    public List<CorpCommerceInfo> listCorpCommerce(LambdaQueryWrapper<CorpCommerceInfo> wrapper) {
        return corpCommerceInfoService.list(wrapper);
    }
}
