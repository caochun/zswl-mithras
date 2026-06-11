package cn.zswltech.mithras.customer.application.client;

import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.model.client.Client;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Optional;

@Service
public class CorpCommerceInfoGroupNameService {

    @Resource
    private ClientMapper clientMapper;

    public String queryBelongGroupClientName(Long clientId, Long belongGroupClientId, String clientName) {
        if (Objects.isNull(belongGroupClientId)) {
            return null;
        }
        if (Objects.equals(-1L, belongGroupClientId)) {
            return "无";
        }
        if (belongGroupClientId.equals(clientId)) {
            return clientName;
        }
        return Optional.ofNullable(clientMapper.selectById(belongGroupClientId))
                .map(Client::getClientName)
                .orElse(null);
    }
}
