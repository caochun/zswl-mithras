package cn.zswltech.mithras.application.adapter.rating;

import cn.zswltech.mithras.dto.client.client.ClientListRSP;
import cn.zswltech.mithras.rating.application.RatingClientSupportPort;
import cn.zswltech.mithras.service.service.client.ClientService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component
public class RatingClientSupportPortAdapter implements RatingClientSupportPort {

    @Resource
    private ClientService clientService;

    @Override
    public Map<Long, Long> getClientRemainingPrincipalMap(List<Long> clientIds) {
        return clientService.getClientRemainingPrincipalMap(clientIds);
    }

    @Override
    public void fillOtherInfo(List<ClientListRSP> list, Boolean showApprovalFlag) {
        clientService.fillOtherInfo(list, showApprovalFlag);
    }
}
