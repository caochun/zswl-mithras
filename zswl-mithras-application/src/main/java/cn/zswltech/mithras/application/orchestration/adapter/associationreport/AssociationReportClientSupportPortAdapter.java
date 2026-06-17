package cn.zswltech.mithras.application.orchestration.adapter.associationreport;

import cn.zswltech.mithras.associationreport.application.AssociationReportClientSnapshot;
import cn.zswltech.mithras.associationreport.application.AssociationReportClientSupportPort;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.customer.enums.client.ClientStatus;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.corp.CorpShareholderInfoMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.ClientBaseModel;
import cn.zswltech.mithras.customer.model.client.CorpShareholderInfo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AssociationReportClientSupportPortAdapter implements AssociationReportClientSupportPort {

    @Resource
    private ClientService clientService;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private CorpShareholderInfoMapper corpShareholderInfoMapper;

    @Override
    public List<AssociationReportClientSnapshot> listEffectiveCorporationClients() {
        return clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                        .eq(Client::getClientType, ClientType.CORPORATION.name())
                        .eq(Client::getClientStatus, ClientStatus.TAKE_EFFECT.name()))
                .stream()
                .map(client -> AssociationReportClientSnapshot.builder()
                        .clientId(client.getId())
                        .clientName(client.getClientName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public AssociationReportClientSnapshot getClient(Long clientId) {
        if (clientId == null) {
            return null;
        }
        Client client = clientMapper.selectById(clientId);
        if (client == null) {
            return null;
        }
        return toSnapshot(client);
    }

    @Override
    public String findMatchedShareholderName(Long clientId, List<String> shareholderNames) {
        Client client = clientMapper.selectById(clientId);
        if (client == null || shareholderNames == null || shareholderNames.isEmpty()) {
            return null;
        }
        if (shareholderNames.contains(client.getClientName())) {
            return client.getClientName();
        }
        return corpShareholderInfoMapper.selectList(Wrappers.<CorpShareholderInfo>lambdaQuery()
                        .eq(ClientBaseModel::getClientId, client.getId()))
                .stream()
                .map(CorpShareholderInfo::getShareholderName)
                .filter(shareholderNames::contains)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Map<Long, Long> getClientRemainingPrincipalMap(List<Long> clientIds, LocalDate targetDate) {
        return clientService.getClientRemainingPrincipalMap(clientIds, targetDate);
    }

    @Override
    public Map<Long, Long> clientStockRiskExposureMap(List<Long> clientIds, LocalDate targetDate) {
        return clientService.clientStockRiskExposureMap(clientIds, targetDate);
    }

    private AssociationReportClientSnapshot toSnapshot(Client client) {
        return AssociationReportClientSnapshot.builder()
                .clientId(client.getId())
                .clientName(client.getClientName())
                .build();
    }
}
