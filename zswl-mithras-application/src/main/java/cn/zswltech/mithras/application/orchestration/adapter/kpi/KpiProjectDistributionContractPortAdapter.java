package cn.zswltech.mithras.application.orchestration.adapter.kpi;

import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.kpi.application.distribution.port.KpiProjectDistributionContractPort;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class KpiProjectDistributionContractPortAdapter implements KpiProjectDistributionContractPort {

    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;

    @Override
    public Map<String, Long> mapEffectiveContractIdsByCodes(Collection<String> contractCodes) {
        if (contractCodes == null || contractCodes.isEmpty()) {
            return Collections.emptyMap();
        }
        return contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                        .in(ContractBaseInfo::getContractCode, contractCodes)
                        .notIn(ContractBaseInfo::getContractStatus, ContractStatus.INVALID.name(), ContractStatus.CLOSED.name()))
                .stream()
                .filter(contract -> contract.getContractCode() != null)
                .collect(Collectors.toMap(ContractBaseInfo::getContractCode, ContractBaseInfo::getId, (a, b) -> a));
    }
}
