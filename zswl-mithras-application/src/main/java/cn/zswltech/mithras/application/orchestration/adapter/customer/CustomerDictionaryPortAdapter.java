package cn.zswltech.mithras.application.orchestration.adapter.customer;

import cn.zswltech.mithras.basedata.persistence.mapper.AddressDictionaryMapper;
import cn.zswltech.mithras.basedata.persistence.mapper.GeneralDictionaryMapper;
import cn.zswltech.mithras.basedata.persistence.model.AddressDictionary;
import cn.zswltech.mithras.basedata.persistence.model.GeneralDictionary;
import cn.zswltech.mithras.customer.application.client.CustomerDictionaryPort;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.foundation.util.Const.ENUM_TYC_PROVINCE;

@Component
public class CustomerDictionaryPortAdapter implements CustomerDictionaryPort {

    @Resource
    private AddressDictionaryMapper addressDictionaryMapper;
    @Resource
    private GeneralDictionaryMapper generalDictionaryMapper;

    @Override
    public Map<String, String> addressCode2Display(Collection<String> addressCodes) {
        if (addressCodes == null || addressCodes.isEmpty()) {
            return Collections.emptyMap();
        }
        return addressDictionaryMapper.selectList(Wrappers.<AddressDictionary>lambdaQuery()
                        .in(AddressDictionary::getCode, addressCodes))
                .stream()
                .collect(Collectors.toMap(AddressDictionary::getCode, AddressDictionary::getDisplay, (a, b) -> a));
    }

    @Override
    public String addressCode2Display(String addressCode) {
        if (addressCode == null) {
            return null;
        }
        AddressDictionary addressDictionary = addressDictionaryMapper.selectOne(Wrappers.<AddressDictionary>lambdaQuery()
                .eq(AddressDictionary::getCode, addressCode));
        return addressDictionary == null ? null : addressDictionary.getDisplay();
    }

    @Override
    public TycAddress resolveTycAddress(String tycProvinceCode, String cityName, String districtName) {
        GeneralDictionary tycProvince = generalDictionaryMapper.selectOne(Wrappers.<GeneralDictionary>lambdaQuery()
                .eq(GeneralDictionary::getDictKey, ENUM_TYC_PROVINCE)
                .eq(GeneralDictionary::getCode, tycProvinceCode));
        if (tycProvince == null) {
            return null;
        }
        AddressDictionary province = addressDictionaryMapper.selectOne(Wrappers.<AddressDictionary>lambdaQuery()
                .eq(AddressDictionary::getDisplay, tycProvince.getDisplay())
                .eq(AddressDictionary::getHistory, YesOrNoNumberEnum.NO.getCode()));
        if (province == null) {
            return null;
        }

        TycAddress tycAddress = new TycAddress();
        tycAddress.setProvinceCode(province.getCode());

        AddressDictionary city = addressDictionaryMapper.selectOne(Wrappers.<AddressDictionary>lambdaQuery()
                .eq(AddressDictionary::getParentId, province.getId())
                .eq(AddressDictionary::getDisplay, cityName)
                .eq(AddressDictionary::getHistory, YesOrNoNumberEnum.NO.getCode()));
        if (city == null) {
            return tycAddress;
        }
        tycAddress.setCityCode(city.getCode());

        AddressDictionary district = addressDictionaryMapper.selectOne(Wrappers.<AddressDictionary>lambdaQuery()
                .eq(AddressDictionary::getParentId, city.getId())
                .eq(AddressDictionary::getDisplay, districtName)
                .eq(AddressDictionary::getHistory, YesOrNoNumberEnum.NO.getCode()));
        if (Objects.nonNull(district)) {
            tycAddress.setDistrictCode(district.getCode());
            tycAddress.setRegionCode(district.getCode());
        }
        return tycAddress;
    }
}
