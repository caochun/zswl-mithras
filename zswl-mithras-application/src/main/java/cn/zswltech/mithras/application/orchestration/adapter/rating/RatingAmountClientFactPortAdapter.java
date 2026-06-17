package cn.zswltech.mithras.application.orchestration.adapter.rating;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.customer.enums.SubjectItemType;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.customer.mapper.corp.CorpSubjectItemMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.customer.model.client.CorpSubjectItem;
import cn.zswltech.mithras.dto.client.client.ClientListRSP;
import cn.zswltech.mithras.rating.application.RatingAmountClientFactPort;
import cn.zswltech.mithras.rating.application.RatingAmountClientSnapshot;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RatingAmountClientFactPortAdapter implements RatingAmountClientFactPort {

    @Resource
    private ClientMapper clientMapper;
    @Resource
    private CorpSubjectItemMapper subjectItemMapper;
    @Resource
    private CorpCommerceInfoMapper corpCommerceInfoMapper;
    @Resource
    private ClientService clientService;

    @Override
    public Map<Long, RatingAmountClientSnapshot> clientSnapshotMap(Collection<Long> clientIds) {
        if (CollectionUtils.isEmpty(clientIds)) {
            return Collections.emptyMap();
        }
        return clientMapper.selectList(Wrappers.<Client>lambdaQuery().in(Client::getId, clientIds))
                .stream()
                .map(client -> BeanUtil.copyProperties(client, RatingAmountClientSnapshot.class))
                .collect(Collectors.toMap(RatingAmountClientSnapshot::getId, v -> v, (m1, m2) -> m1));
    }

    @Override
    public RatingAmountClientSnapshot clientSnapshot(Long clientId) {
        if (clientId == null) {
            return null;
        }
        Client client = clientMapper.selectById(clientId);
        return Optional.ofNullable(client)
                .map(item -> BeanUtil.copyProperties(item, RatingAmountClientSnapshot.class))
                .orElse(null);
    }

    @Override
    public Long latestOperatingIncome(Long clientId) {
        CorpSubjectItem subjectItem = subjectItemMapper.selectOne(Wrappers.<CorpSubjectItem>lambdaQuery()
                .eq(CorpSubjectItem::getClientId, clientId)
                .eq(CorpSubjectItem::getSubjectCode, "H9170")
                .eq(CorpSubjectItem::getSubjectType, SubjectItemType.PROFIT.name())
                .orderByDesc(CorpSubjectItem::getYear)
                .orderByDesc(CorpSubjectItem::getQuarter)
                .last("limit 1"));
        return Optional.ofNullable(subjectItem).map(CorpSubjectItem::getSubjectValue).orElse(null);
    }

    @Override
    public Long groupClientId(Long clientId) {
        CorpCommerceInfo corpCommerceInfo = corpCommerceInfoMapper.selectOne(Wrappers.<CorpCommerceInfo>lambdaQuery()
                .eq(CorpCommerceInfo::getClientId, clientId));
        if (corpCommerceInfo == null) {
            return null;
        }
        Long belongGroupClientId = corpCommerceInfo.getBelongGroupClientId();
        if (belongGroupClientId == null || belongGroupClientId == -1L) {
            return corpCommerceInfo.getClientId();
        }
        return belongGroupClientId;
    }

    @Override
    public List<Long> groupMemberClientIds(Long clientId) {
        Long groupClientId = groupClientId(clientId);
        if (groupClientId == null) {
            return Collections.emptyList();
        }
        return corpCommerceInfoMapper.selectList(Wrappers.<CorpCommerceInfo>lambdaQuery()
                        .eq(CorpCommerceInfo::getBelongGroupClientId, groupClientId)
                        .or()
                        .eq(CorpCommerceInfo::getClientId, groupClientId))
                .stream()
                .map(CorpCommerceInfo::getClientId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> unsettledClientIds(List<Long> clientIds) {
        if (CollectionUtils.isEmpty(clientIds)) {
            return Collections.emptyList();
        }
        List<Client> clients = clientMapper.selectBatchIds(clientIds);
        List<ClientListRSP> clientListRSPS = BeanUtil.copyToList(clients, ClientListRSP.class);
        clientService.fillOtherInfo(clientListRSPS, false);
        return clientListRSPS.stream()
                .filter(item -> Optional.ofNullable(item.getLastPrincipal()).orElse(0L) > 0)
                .map(ClientListRSP::getId)
                .collect(Collectors.toList());
    }
}
