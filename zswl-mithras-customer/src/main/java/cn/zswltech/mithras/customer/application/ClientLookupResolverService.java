package cn.zswltech.mithras.customer.application;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.customer.hymx.mapper.ClientHymxMapper;
import cn.zswltech.mithras.customer.hymx.model.ClientHymx;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.foundation.port.ClientDeptResolver;
import cn.zswltech.mithras.foundation.port.ClientInfoResolver;
import cn.zswltech.mithras.foundation.port.ClientNameResolver;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.CollUtil.isNotEmpty;

@Service
public class ClientLookupResolverService implements ClientNameResolver, ClientInfoResolver, ClientDeptResolver {

    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ClientHymxMapper clientHymxMapper;

    @Override
    public Map<Long, String> clientId2Name(Collection<Long> clientIds) {
        Map<Long, String> result = new HashMap<>();
        if (isNotEmpty(clientIds)) {
            Set<Long> ids = new HashSet<>(clientIds);
            result = clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                    .in(Client::getId, ids)
            ).stream().collect(Collectors.toMap(Client::getId, Client::getClientName));

            if (ids.size() > result.size()) {
                Map<Long, String> hymxResult = clientHymxMapper.selectList(Wrappers.<ClientHymx>lambdaQuery()
                        .in(ClientHymx::getId, ids)
                ).stream().collect(Collectors.toMap(ClientHymx::getId, ClientHymx::getClientName));
                result.putAll(hymxResult);
            }
        }
        return result;
    }

    @Override
    public Map<Long, ClientInfo> clientId2Client(Collection<Long> clientIds) {
        Map<Long, ClientInfo> result = new HashMap<>();
        if (isNotEmpty(clientIds)) {
            clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                    .in(Client::getId, clientIds)
            ).forEach(client -> {
                ClientInfo clientInfo = new ClientInfo();
                BeanUtil.copyProperties(client, clientInfo);
                clientInfo.setClientId(client.getId());
                result.put(client.getId(), clientInfo);
            });
        }
        return result;
    }

    @Override
    public Map<Long, Long> clientId2DeptId(Collection<Long> clientIds) {
        Map<Long, Long> result = new HashMap<>();
        if (isNotEmpty(clientIds)) {
            clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                    .in(Client::getId, clientIds)
            ).forEach(client -> result.put(client.getId(), client.getBelongDeptId()));
        }
        return result;
    }
}
