package cn.zswltech.mithras.riskcontrol.scorecard.infrastructure;

import cn.zswltech.mithras.basedata.mapper.AddressDictionaryMapper;
import cn.zswltech.mithras.basedata.mapper.model.AddressDictionary;
import cn.zswltech.mithras.customer.domain.enums.CorpAddressType;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.CorpAddressInfoMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.ClientBaseModel;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpAddressInfo;
import cn.zswltech.mithras.riskcontrol.scorecard.application.ScoreCardClientAddressResolver;
import cn.zswltech.mithras.riskcontrol.scorecard.application.ScoreCardClientRegistryAddress;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ScoreCardClientAddressResolverImpl implements ScoreCardClientAddressResolver {

    @Resource
    private CorpAddressInfoMapper corpAddressInfoMapper;
    @Resource
    private AddressDictionaryMapper addressDictionaryMapper;

    @Override
    public Optional<ScoreCardClientRegistryAddress> registryAddress(Long clientId) {
        List<CorpAddressInfo> addresses = corpAddressInfoMapper.selectList(Wrappers.<CorpAddressInfo>lambdaQuery()
                .eq(ClientBaseModel::getClientId, clientId));
        Map<String, String> addressNameMap = addressNameMap(addresses);
        return addresses.stream()
                .filter(address -> CorpAddressType.REGISTRY_ADDRESS.name().equals(address.getAddressType()))
                .findFirst()
                .map(address -> new ScoreCardClientRegistryAddress(
                        addressNameMap.get(address.getProvince()),
                        addressNameMap.get(address.getCity()),
                        addressNameMap.get(address.getDistrict())));
    }

    private Map<String, String> addressNameMap(List<CorpAddressInfo> addresses) {
        Set<String> codeSet = new HashSet<>(addresses.size() * 3);
        addresses.forEach(address -> {
            codeSet.add(address.getCountry());
            codeSet.add(address.getProvince());
            codeSet.add(address.getCity());
            codeSet.add(address.getDistrict());
        });
        codeSet.remove(null);
        if (codeSet.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        return addressDictionaryMapper.selectList(Wrappers.<AddressDictionary>lambdaQuery()
                        .in(AddressDictionary::getCode, codeSet))
                .stream()
                .collect(Collectors.toMap(AddressDictionary::getCode, AddressDictionary::getDisplay, (first, second) -> second));
    }
}
