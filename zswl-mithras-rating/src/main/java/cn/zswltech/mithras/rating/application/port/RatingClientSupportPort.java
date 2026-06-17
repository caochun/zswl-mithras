package cn.zswltech.mithras.rating.application.port;

import cn.zswltech.mithras.dto.client.client.ClientListRSP;

import java.util.List;
import java.util.Map;

public interface RatingClientSupportPort {

    Map<Long, Long> getClientRemainingPrincipalMap(List<Long> clientIds);

    void fillOtherInfo(List<ClientListRSP> list, Boolean showApprovalFlag);
}
