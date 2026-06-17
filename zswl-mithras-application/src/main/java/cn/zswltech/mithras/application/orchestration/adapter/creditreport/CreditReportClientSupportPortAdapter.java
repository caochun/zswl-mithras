package cn.zswltech.mithras.application.orchestration.adapter.creditreport;

import cn.zswltech.mithras.creditreport.service.CreditReportClientSupportPort;
import cn.zswltech.mithras.creditreport.service.CreditReportClientBusinessSnapshot;
import cn.zswltech.mithras.creditreport.service.CreditReportClientCommerceSnapshot;
import cn.zswltech.mithras.creditreport.service.CreditReportClientSnapshot;
import cn.zswltech.mithras.customer.application.client.model.ClientBusinessHistoryBO;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.customer.application.client.dto.MithrasShareholderInfo;
import cn.zswltech.mithras.customer.application.client.ClientBusinessHistoryService;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.customer.mapper.corp.CorpShareholderInfoMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.customer.model.client.CorpShareholderInfo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CreditReportClientSupportPortAdapter implements CreditReportClientSupportPort {

    @Resource
    private ClientService clientService;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private CorpCommerceInfoMapper corpCommerceInfoMapper;
    @Resource
    private CorpShareholderInfoMapper corpShareholderInfoMapper;
    @Resource
    private ClientBusinessHistoryService clientBusinessHistoryService;

    @Override
    public Set<Long> findTargetClientIdsByUserId(Long userId) {
        return clientService.findTargetClientIdsByUserId(userId);
    }

    @Override
    public Set<Long> findTargetClientIdsByDeptIds(List<Long> deptIds) {
        return clientService.findTargetClientIdsByDeptIds(deptIds);
    }

    @Override
    public Map<Long, CreditReportClientBusinessSnapshot> compareBusiness(List<Long> clientIds) {
        return clientService.compareBusiness(clientIds).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> toSnapshot(entry.getValue()), (a, b) -> a));
    }

    @Override
    public Map<Long, CreditReportClientBusinessSnapshot> getHistoryBusiness(List<Long> clientIds) {
        return clientBusinessHistoryService.getHistoryBoByClientIds(clientIds).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> toSnapshot(entry.getValue()), (a, b) -> a));
    }

    @Override
    public CreditReportClientSnapshot getClient(Long clientId) {
        Client client = clientMapper.selectById(clientId);
        return client == null ? null : toClientSnapshot(client);
    }

    @Override
    public List<CreditReportClientSnapshot> listCorporationClients(List<Long> clientIds) {
        return clientMapper.selectBatchIds(clientIds).stream()
                .filter(item -> ClientType.CORPORATION.name().equals(item.getClientType()))
                .map(this::toClientSnapshot)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, CreditReportClientCommerceSnapshot> listCommerceSnapshots(List<Long> clientIds) {
        Map<Long, CorpCommerceInfo> corpCommerceMap = corpCommerceInfoMapper.selectList(Wrappers.<CorpCommerceInfo>lambdaQuery()
                        .in(CorpCommerceInfo::getClientId, clientIds))
                .stream()
                .collect(Collectors.toMap(CorpCommerceInfo::getClientId, e -> e, (a, b) -> a));
        Map<Long, List<CorpShareholderInfo>> clientShareholderMap = corpShareholderInfoMapper.selectList(Wrappers.<CorpShareholderInfo>lambdaQuery()
                        .in(CorpShareholderInfo::getClientId, clientIds))
                .stream()
                .collect(Collectors.groupingBy(CorpShareholderInfo::getClientId));

        return clientMapper.selectBatchIds(clientIds).stream()
                .collect(Collectors.toMap(Client::getId,
                        client -> toCommerceSnapshot(client, corpCommerceMap.get(client.getId()), clientShareholderMap.get(client.getId())),
                        (a, b) -> a));
    }

    private CreditReportClientBusinessSnapshot toSnapshot(ClientBusinessHistoryBO bo) {
        if (bo == null) {
            return null;
        }
        CreditReportClientBusinessSnapshot snapshot = new CreditReportClientBusinessSnapshot();
        snapshot.setClientId(bo.getClientId());
        snapshot.setTycName(bo.getTycName());
        snapshot.setTycCorpRepresent(bo.getTycCorpRepresent());
        if (bo.getTycShareHolderInfo() != null) {
            snapshot.setTycShareHolderInfo(bo.getTycShareHolderInfo().stream()
                    .map(this::toSnapshot)
                    .collect(Collectors.toList()));
        }
        return snapshot;
    }

    private CreditReportClientBusinessSnapshot.ShareholderSnapshot toSnapshot(MithrasShareholderInfo info) {
        CreditReportClientBusinessSnapshot.ShareholderSnapshot snapshot = new CreditReportClientBusinessSnapshot.ShareholderSnapshot();
        snapshot.setShareholderName(info.getShareholderName());
        snapshot.setCapitalPercent(info.getCapitalPercent());
        return snapshot;
    }

    private CreditReportClientSnapshot toClientSnapshot(Client client) {
        CreditReportClientSnapshot snapshot = new CreditReportClientSnapshot();
        snapshot.setClientId(client.getId());
        snapshot.setClientName(client.getClientName());
        snapshot.setCscCode(client.getUscCode());

        CorpCommerceInfo corpCommerceInfo = corpCommerceInfoMapper.selectOne(
                Wrappers.<CorpCommerceInfo>lambdaQuery()
                        .eq(CorpCommerceInfo::getClientId, client.getId())
        );
        Optional.ofNullable(corpCommerceInfo)
                .map(CorpCommerceInfo::getZhongZhengCode)
                .filter(StringUtils::isNotBlank)
                .ifPresent(snapshot::setZhongZhengCode);
        return snapshot;
    }

    private CreditReportClientCommerceSnapshot toCommerceSnapshot(Client client, CorpCommerceInfo commerceInfo, List<CorpShareholderInfo> shareholderInfos) {
        CreditReportClientCommerceSnapshot snapshot = new CreditReportClientCommerceSnapshot();
        snapshot.setClientId(client.getId());
        snapshot.setClientName(client.getClientName());
        snapshot.setClientType(client.getClientType());
        snapshot.setCorporation(ClientType.CORPORATION.name().equals(client.getClientType()));
        snapshot.setCorpRepresent(Optional.ofNullable(commerceInfo).map(CorpCommerceInfo::getCorpRepresent).orElse(null));
        if (shareholderInfos != null) {
            snapshot.setShareholders(shareholderInfos.stream()
                    .map(this::toCommerceShareholderSnapshot)
                    .collect(Collectors.toList()));
        }
        return snapshot;
    }

    private CreditReportClientCommerceSnapshot.ShareholderSnapshot toCommerceShareholderSnapshot(CorpShareholderInfo info) {
        CreditReportClientCommerceSnapshot.ShareholderSnapshot snapshot = new CreditReportClientCommerceSnapshot.ShareholderSnapshot();
        snapshot.setShareholderName(info.getShareholderName());
        snapshot.setCapitalPercent(info.getCapitalPercent());
        return snapshot;
    }
}
