package cn.zswltech.mithras.customer.application.client;

import cn.zswltech.mithras.customer.application.client.model.ClientAuthBO;
import cn.zswltech.mithras.customer.application.client.model.ClientBusinessHistoryBO;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.customer.model.client.CorpShareholderInfo;
import cn.zswltech.mithras.dto.contract.ContractCompareBusinessRSP;

import java.util.List;
import java.util.Map;

public interface CorpCommerceInfoSupportPort {

    ClientAuthBO getClientAuthByProj(Long clientId, Long userId);

    Map<Long, ClientBusinessHistoryBO> compareBusiness(List<Long> clientIds);

    ContractCompareBusinessRSP buildContractCompareBusinessRsp(ClientBusinessHistoryBO bo,
                                                               Client client,
                                                               CorpCommerceInfo corpCommerceInfo,
                                                               List<CorpShareholderInfo> corpShareholderInfos,
                                                               String clientType);
}
