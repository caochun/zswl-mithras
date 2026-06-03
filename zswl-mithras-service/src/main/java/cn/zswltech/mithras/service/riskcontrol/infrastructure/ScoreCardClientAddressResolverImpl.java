package cn.zswltech.mithras.service.riskcontrol.infrastructure;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.client.addressinfo.CorpAddressInfoListREQ;
import cn.zswltech.mithras.dto.client.addressinfo.CorpAddressInfoListRSP;
import cn.zswltech.mithras.riskcontrol.scorecard.application.ScoreCardClientAddressResolver;
import cn.zswltech.mithras.riskcontrol.scorecard.application.ScoreCardClientRegistryAddress;
import cn.zswltech.mithras.customer.domain.enums.CorpAddressType;
import cn.zswltech.mithras.basedata.mapper.AddressDictionaryMapper;
import cn.zswltech.mithras.basedata.mapper.model.AddressDictionary;
import cn.zswltech.mithras.service.service.client.CorpAddressInfoService;
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
    private CorpAddressInfoService corpAddressInfoService;
    @Resource
    private AddressDictionaryMapper addressDictionaryMapper;

    @Override
    public Optional<ScoreCardClientRegistryAddress> registryAddress(Long clientId) {
        CorpAddressInfoListREQ req = new CorpAddressInfoListREQ();
        req.setClientId(clientId);
        List<CorpAddressInfoListRSP> addresses = BeanUtil.copyToList(corpAddressInfoService.list(req).getRecords(), CorpAddressInfoListRSP.class);
        fillAddressNames(addresses);
        return addresses.stream()
                .filter(address -> CorpAddressType.REGISTRY_ADDRESS.name().equals(address.getAddressType()))
                .findFirst()
                .map(address -> new ScoreCardClientRegistryAddress(
                        address.getProvinceName(),
                        address.getCityName(),
                        address.getDistrictName()));
    }

    private void fillAddressNames(List<CorpAddressInfoListRSP> addresses) {
        Set<String> codeSet = new HashSet<>(addresses.size() * 3);
        addresses.forEach(address -> {
            codeSet.add(address.getCountry());
            codeSet.add(address.getProvince());
            codeSet.add(address.getCity());
            codeSet.add(address.getDistrict());
        });
        codeSet.remove(null);
        if (codeSet.isEmpty()) {
            return;
        }
        Map<String, String> nameMap = addressDictionaryMapper.selectList(Wrappers.<AddressDictionary>lambdaQuery()
                        .in(AddressDictionary::getCode, codeSet))
                .stream()
                .collect(Collectors.toMap(AddressDictionary::getCode, AddressDictionary::getDisplay, (first, second) -> second));
        addresses.forEach(address -> {
            address.setCountryName(nameMap.get(address.getCountry()));
            address.setProvinceName(nameMap.get(address.getProvince()));
            address.setCityName(nameMap.get(address.getCity()));
            address.setDistrictName(nameMap.get(address.getDistrict()));
        });
    }
}
