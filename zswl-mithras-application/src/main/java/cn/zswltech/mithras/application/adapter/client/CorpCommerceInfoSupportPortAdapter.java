package cn.zswltech.mithras.application.adapter.client;

import cn.zswltech.mithras.customer.application.bo.ClientAuthBO;
import cn.zswltech.mithras.customer.application.bo.ClientBusinessHistoryBO;
import cn.zswltech.mithras.customer.application.client.CorpCommerceInfoSupportPort;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpShareholderInfo;
import cn.zswltech.mithras.dto.contract.ContractCompareBusinessRSP;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.contract.impl.ContractBaseInfoServiceImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component
public class CorpCommerceInfoSupportPortAdapter implements CorpCommerceInfoSupportPort {

    @Resource
    private ClientService clientService;

    @Resource
    private ContractBaseInfoServiceImpl contractBaseInfoService;

    @Override
    public ClientAuthBO getClientAuthByProj(Long clientId, Long userId) {
        return clientService.getClientAuthByProj(clientId, userId);
    }

    @Override
    public Map<Long, ClientBusinessHistoryBO> compareBusiness(List<Long> clientIds) {
        return clientService.compareBusiness(clientIds);
    }

    @Override
    public ContractCompareBusinessRSP buildContractCompareBusinessRsp(ClientBusinessHistoryBO bo,
                                                                      Client client,
                                                                      CorpCommerceInfo corpCommerceInfo,
                                                                      List<CorpShareholderInfo> corpShareholderInfos,
                                                                      String clientType) {
        return contractBaseInfoService.buildContractCompareBusinessRSP(bo, client, corpCommerceInfo, corpShareholderInfos, clientType);
    }
}
