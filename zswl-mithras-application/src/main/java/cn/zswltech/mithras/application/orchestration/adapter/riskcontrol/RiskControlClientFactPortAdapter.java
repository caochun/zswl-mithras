package cn.zswltech.mithras.application.orchestration.adapter.riskcontrol;

import cn.zswltech.mithras.customer.mapper.lib.client.CorpCommerceInfoLibMapper;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.customer.versioning.dto.CorpCommerceInfoLibDto;
import cn.zswltech.mithras.customer.application.client.ClientProvinceQueryService;
import cn.zswltech.mithras.customer.enums.client.ClientStatus;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.corp.IndustryTypeMapper;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpAddressInfoLibMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.ClientBaseModel;
import cn.zswltech.mithras.customer.model.client.CorpAddressInfo;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.foundation.enums.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.riskcontrol.application.port.RiskControlConcentrationClientFact;
import cn.zswltech.mithras.riskcontrol.application.port.RiskControlClientFactPort;
import cn.zswltech.mithras.customer.model.client.IndustryType;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RiskControlClientFactPortAdapter implements RiskControlClientFactPort {

    @Resource
    private CorpCommerceInfoLibMapper corpCommerceInfoLibMapper;
    @Resource
    private ClientProvinceQueryService clientProvinceQueryService;
    @Resource
    private IndustryTypeMapper industryTypeMapper;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private CorpAddressInfoLibMapper corpAddressInfoLibMapper;

    @Override
    public Set<Long> relatedClientIds() {
        CorpCommerceInfoLibDto dto = new CorpCommerceInfoLibDto();
        dto.setIsRelated(1);
        return corpCommerceInfoLibMapper.listNewestCommerceInfo(dto)
                .stream()
                .map(CorpCommerceInfoLib::getClientId)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Long> intraGroupClientIds() {
        return corpCommerceInfoLibMapper.intraGroupClients()
                .stream()
                .map(CorpCommerceInfoLib::getClientId)
                .collect(Collectors.toSet());
    }

    @Override
    public Map<Long, Long> intraGroupClientIdToGroupId() {
        return clientIdToGroupId(corpCommerceInfoLibMapper.intraGroupClients());
    }

    @Override
    public Set<Long> nonZhejiangNonIntraGroupClientIds() {
        return nonZhejiangNonIntraGroupClients()
                .stream()
                .map(CorpCommerceInfoLib::getClientId)
                .collect(Collectors.toSet());
    }

    @Override
    public Map<Long, Long> nonZhejiangNonIntraGroupClientIdToGroupId() {
        return clientIdToGroupId(nonZhejiangNonIntraGroupClients());
    }

    @Override
    public Set<Long> zhejiangClientIds() {
        return zhejiangClients()
                .stream()
                .map(CorpCommerceInfoLib::getClientId)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Long> nonZhejiangClientIds() {
        return clientProvinceQueryService.getNotInSpecifyProvinceClientIds(Collections.singletonList("330000"));
    }

    @Override
    public Map<Long, Long> zhejiangClientIdToGroupId() {
        return clientIdToGroupId(zhejiangClients());
    }

    @Override
    public Set<Long> clientIdsNotInRiskControlIndustryClassify(Set<String> classifications) {
        CorpCommerceInfoLibDto dto = new CorpCommerceInfoLibDto();
        dto.setNotInRiskControlIndustryClassify(newList(classifications));
        return clientIds(corpCommerceInfoLibMapper.listNewestCommerceInfo(dto));
    }

    @Override
    public Set<Long> clientIdsInRiskControlIndustryClassify(Set<String> classifications) {
        CorpCommerceInfoLibDto dto = new CorpCommerceInfoLibDto();
        dto.setInRiskControlIndustryClassify(newList(classifications));
        return clientIds(corpCommerceInfoLibMapper.listNewestCommerceInfo(dto));
    }

    @Override
    public Map<Long, String> clientIdToIndustryTypeInRiskControlIndustryClassify(Set<String> classifications) {
        CorpCommerceInfoLibDto dto = new CorpCommerceInfoLibDto();
        dto.setInRiskControlIndustryClassify(newList(classifications));
        return corpCommerceInfoLibMapper.listNewestCommerceInfo(dto)
                .stream()
                .collect(Collectors.toMap(CorpCommerceInfoLib::getClientId,
                        CorpCommerceInfo::getIndustryType, (a, b) -> a));
    }

    @Override
    public Map<String, String> twoLevelIndustryTypeNameMap() {
        return industryTypeMapper.selectList(Wrappers.<IndustryType>lambdaQuery()
                        .eq(IndustryType::getLevel, 2))
                .stream()
                .collect(Collectors.toMap(IndustryType::getCode, IndustryType::getDisplay));
    }

    @Override
    public String riskControlIndustryClassify(Long clientId) {
        CorpCommerceInfoLib commerceInfo = corpCommerceInfoLibMapper
                .selectOne(Wrappers.<CorpCommerceInfoLib>lambdaQuery()
                        .eq(CorpCommerceInfoLib::getClientId, clientId)
                        .eq(CorpCommerceInfoLib::getVersionType, VersionTypeConstants.NORMAL)
                        .orderByDesc(CorpCommerceInfoLib::getVersion)
                        .last("limit 1"));
        if (commerceInfo == null) {
            throw new MithrasException("不存在该客户的生效信息");
        }
        return commerceInfo.getRiskControlIndustryClassify();
    }

    @Override
    public Map<String, Long> activeClientIdsByCreditCodes(Set<String> creditCodes) {
        if (creditCodes == null || creditCodes.isEmpty()) {
            return Collections.emptyMap();
        }
        return clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                        .eq(Client::getClientStatus, ClientStatus.TAKE_EFFECT.name())
                        .in(Client::getUscCode, creditCodes))
                .stream()
                .filter(client -> client.getUscCode() != null && !client.getUscCode().isEmpty())
                .collect(Collectors.toMap(Client::getUscCode, Client::getId, (a, b) -> a));
    }

    @Override
    public Map<Long, RiskControlConcentrationClientFact> concentrationClientFacts() {
        List<CorpCommerceInfoLib> commerceInfos = corpCommerceInfoLibMapper.listNewestCommerceInfo(new CorpCommerceInfoLibDto());
        Set<Long> clientIds = commerceInfos.stream()
                .map(CorpCommerceInfoLib::getClientId)
                .collect(Collectors.toSet());
        Map<Long, String> registryAddress = corpAddressInfoLibMapper.listNewestAddressByClientId(clientIds)
                .stream()
                .collect(Collectors.toMap(ClientBaseModel::getClientId, CorpAddressInfo::getProvince, (a, b) -> b));
        Map<Long, Long> clientSponsorMap = clientMapper.selectBatchIds(clientIds)
                .stream()
                .filter(client -> client.getBelongSponsorId() != null)
                .collect(Collectors.toMap(Client::getId, Client::getBelongSponsorId, (a, b) -> a));
        return commerceInfos.stream()
                .collect(Collectors.toMap(CorpCommerceInfoLib::getClientId,
                        commerceInfo -> concentrationClientFact(commerceInfo, registryAddress, clientSponsorMap),
                        (a, b) -> b));
    }

    @Override
    public Set<Long> clientIdsInRangeNotInRiskControlIndustryClassify(Set<Long> clientIds, Set<String> classifications) {
        if (clientIds == null || clientIds.isEmpty()) {
            return new HashSet<>();
        }
        CorpCommerceInfoLibDto dto = new CorpCommerceInfoLibDto();
        dto.setInClientIds(clientIds);
        dto.setNotInRiskControlIndustryClassify(newList(classifications));
        return clientIds(corpCommerceInfoLibMapper.listNewestCommerceInfo(dto));
    }

    private List<CorpCommerceInfoLib> nonZhejiangNonIntraGroupClients() {
        Set<Long> clientsInZhejiang = clientProvinceQueryService.getSpecifyProvinceClientIds(Collections.singletonList("330000"));
        CorpCommerceInfoLibDto dto = new CorpCommerceInfoLibDto();
        dto.setNotInRiskControlIndustryClassify(Collections.singletonList(RiskControlIndustryClassify.INTRA_GROUP_COLLABORATION.name()));
        dto.setNotInClientIds(clientsInZhejiang);
        return corpCommerceInfoLibMapper.listNewestCommerceInfo(dto);
    }

    private List<CorpCommerceInfoLib> zhejiangClients() {
        Set<Long> clientsInZhejiang = clientProvinceQueryService.getSpecifyProvinceClientIds(Collections.singletonList("330000"));
        CorpCommerceInfoLibDto dto = new CorpCommerceInfoLibDto();
        dto.setInClientIds(clientsInZhejiang);
        return corpCommerceInfoLibMapper.listNewestCommerceInfo(dto);
    }

    private Set<Long> clientIds(List<CorpCommerceInfoLib> clients) {
        return clients.stream()
                .map(CorpCommerceInfoLib::getClientId)
                .collect(Collectors.toSet());
    }

    private <T> List<T> newList(Set<T> values) {
        return values == null ? Collections.emptyList() : values.stream().collect(Collectors.toList());
    }

    private RiskControlConcentrationClientFact concentrationClientFact(CorpCommerceInfoLib commerceInfo,
                                                                      Map<Long, String> registryAddress,
                                                                      Map<Long, Long> clientSponsorMap) {
        RiskControlConcentrationClientFact fact = new RiskControlConcentrationClientFact();
        fact.setClientId(commerceInfo.getClientId());
        fact.setGroupId(commerceInfo.getBelongGroupClientId());
        fact.setRelated(commerceInfo.getIsRelated());
        fact.setRiskControlIndustryClassify(commerceInfo.getRiskControlIndustryClassify());
        fact.setProvince(registryAddress.getOrDefault(commerceInfo.getClientId(), ""));
        fact.setSponsorId(clientSponsorMap.get(commerceInfo.getClientId()));
        return fact;
    }

    private Map<Long, Long> clientIdToGroupId(List<CorpCommerceInfoLib> clients) {
        return clients.stream()
                .filter(lib -> lib.getBelongGroupClientId() != null && lib.getBelongGroupClientId() != -1L)
                .collect(Collectors.toMap(CorpCommerceInfoLib::getClientId,
                        CorpCommerceInfoLib::getBelongGroupClientId, (a, b) -> a));
    }
}
