package cn.zswltech.mithras.application.orchestration.adapter.metric;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.customer.mapper.corp.IndustryTypeMapper;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpCommerceInfoLibMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.customer.model.client.IndustryType;
import cn.zswltech.mithras.customer.versioning.dto.CorpCommerceInfoLibDto;
import cn.zswltech.mithras.metric.service.MetricCorpCommerceSnapshot;
import cn.zswltech.mithras.metric.service.MetricCustomerInfoPort;
import cn.zswltech.mithras.metric.service.MetricCustomerSnapshot;
import cn.zswltech.mithras.metric.service.MetricIndustryTypeSnapshot;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MetricCustomerInfoPortAdapter implements MetricCustomerInfoPort {

    @Resource
    private ClientMapper clientMapper;
    @Resource
    private CorpCommerceInfoMapper commerceInfoMapper;
    @Resource
    private IndustryTypeMapper industryTypeMapper;
    @Resource
    private CorpCommerceInfoLibMapper commerceInfoLibMapper;

    @Override
    public Map<Long, MetricCustomerSnapshot> mapClientsByIds(Collection<Long> clientIds) {
        if (CollUtil.isEmpty(clientIds)) {
            return Collections.emptyMap();
        }
        Set<Long> filteredClientIds = clientIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());
        if (CollUtil.isEmpty(filteredClientIds)) {
            return Collections.emptyMap();
        }
        return clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                        .in(Client::getId, filteredClientIds))
                .stream()
                .map(item -> new MetricCustomerSnapshot(item.getId(), item.getClientName(), item.getUscCode()))
                .collect(Collectors.toMap(MetricCustomerSnapshot::getId, item -> item, (left, right) -> left));
    }

    @Override
    public Map<Long, MetricCorpCommerceSnapshot> mapCommerceByClientIds(Collection<Long> clientIds) {
        if (CollUtil.isEmpty(clientIds)) {
            return Collections.emptyMap();
        }
        Set<Long> filteredClientIds = clientIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());
        if (CollUtil.isEmpty(filteredClientIds)) {
            return Collections.emptyMap();
        }
        return commerceInfoMapper.selectList(Wrappers.<CorpCommerceInfo>lambdaQuery()
                        .in(CorpCommerceInfo::getClientId, filteredClientIds))
                .stream()
                .map(item -> new MetricCorpCommerceSnapshot(item.getClientId(), item.getIndustryType(), item.getBelongGroupClientId()))
                .collect(Collectors.toMap(MetricCorpCommerceSnapshot::getClientId, item -> item, (left, right) -> left));
    }

    @Override
    public Map<String, MetricIndustryTypeSnapshot> mapIndustryByCodes(Collection<String> industryCodes) {
        if (CollUtil.isEmpty(industryCodes)) {
            return Collections.emptyMap();
        }
        Set<String> filteredIndustryCodes = industryCodes.stream().filter(Objects::nonNull).collect(Collectors.toSet());
        if (CollUtil.isEmpty(filteredIndustryCodes)) {
            return Collections.emptyMap();
        }
        return industryTypeMapper.selectList(Wrappers.<IndustryType>lambdaQuery()
                        .in(IndustryType::getCode, filteredIndustryCodes))
                .stream()
                .map(item -> new MetricIndustryTypeSnapshot(item.getCode(), item.getDisplay()))
                .collect(Collectors.toMap(MetricIndustryTypeSnapshot::getCode, item -> item, (left, right) -> left));
    }

    @Override
    public List<MetricCorpCommerceSnapshot> listNewestCommerceInfo() {
        return listNewestCommerceInfo(new CorpCommerceInfoLibDto());
    }

    @Override
    public List<MetricCorpCommerceSnapshot> listNewestCommerceInfo(Set<Long> clientIds) {
        if (CollUtil.isEmpty(clientIds)) {
            return Collections.emptyList();
        }
        CorpCommerceInfoLibDto dto = new CorpCommerceInfoLibDto();
        dto.setInClientIds(clientIds);
        return listNewestCommerceInfo(dto);
    }

    private List<MetricCorpCommerceSnapshot> listNewestCommerceInfo(CorpCommerceInfoLibDto dto) {
        return commerceInfoLibMapper.listNewestCommerceInfo(dto)
                .stream()
                .map(this::toSnapshot)
                .collect(Collectors.toList());
    }

    private MetricCorpCommerceSnapshot toSnapshot(CorpCommerceInfoLib item) {
        return new MetricCorpCommerceSnapshot(item.getClientId(), item.getIndustryType(), item.getBelongGroupClientId());
    }
}
