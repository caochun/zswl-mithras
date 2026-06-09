package cn.zswltech.mithras.service.adapter.fund;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.third.datashare.mapper.DataShareMerchantsMapper;
import cn.zswltech.mithras.third.datashare.mapper.model.DataShareMerchants;
import cn.zswltech.mithras.fund.application.FundOrganizationInstitutionPort;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FundOrganizationInstitutionPortAdapter implements FundOrganizationInstitutionPort {

    @Resource
    private DataShareMerchantsMapper dataShareMerchantsMapper;

    @Override
    public Map<String, Long> clientIdMapByCreditCodes(Collection<String> creditCodes) {
        if (CollectionUtil.isEmpty(creditCodes)) {
            return Collections.emptyMap();
        }
        return dataShareMerchantsMapper.selectList(Wrappers.<DataShareMerchants>lambdaQuery()
                        .in(DataShareMerchants::getCreditCode, creditCodes))
                .stream()
                .filter(dataShareMerchants -> StrUtil.isNotBlank(dataShareMerchants.getCreditCode()))
                .collect(Collectors.toMap(DataShareMerchants::getCreditCode, DataShareMerchants::getClientId, (k1, k2) -> k1));
    }

    @Override
    public Long findClientIdByCreditCode(String creditCode) {
        DataShareMerchants dataShareMerchants = dataShareMerchantsMapper.selectOne(Wrappers.<DataShareMerchants>lambdaQuery()
                .eq(DataShareMerchants::getCreditCode, creditCode)
                .last("limit 1"));
        return dataShareMerchants == null ? null : dataShareMerchants.getClientId();
    }
}
