package cn.zswltech.mithras.application.orchestration.adapter.third;

import cn.zswltech.gruul.common.util.StringUtil;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.third.datashare.service.port.DataShareClientCodeSyncPort;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class DataShareClientCodeSyncPortAdapter implements DataShareClientCodeSyncPort {

    @Resource
    private ClientMapper clientMapper;

    @Override
    public void syncClientCodes(Map<String, String> clientCodeMap) {
        if (clientCodeMap == null || clientCodeMap.isEmpty()) {
            return;
        }
        List<Client> clients = clientMapper.selectList(Wrappers.<Client>lambdaQuery().isNull(Client::getClientCode));
        List<Client> clientsUpdate = new ArrayList<>();
        clients.forEach(client -> {
            if (ClientType.CORPORATION.name().equals(client.getClientType()) && !StringUtil.isBlank(clientCodeMap.get(client.getUscCode()))) {
                client.setClientCode(clientCodeMap.get(client.getUscCode()));
                clientsUpdate.add(client);
            } else if (!StringUtil.isBlank(clientCodeMap.get(client.getCertNumber()))) {
                client.setClientCode(clientCodeMap.get(client.getCertNumber()));
                clientsUpdate.add(client);
            }
        });
        clientsUpdate.forEach(clientMapper::updateById);
    }
}
