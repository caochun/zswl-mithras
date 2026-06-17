package cn.zswltech.mithras.application.orchestration.adapter.riskcontrol;

import cn.zswltech.mithras.contract.mapper.lib.contract.ContractBaseInfoLibMapper;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.riskcontrol.application.port.RiskControlContractFactPort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RiskControlContractFactPortAdapter implements RiskControlContractFactPort {

    @Resource
    private ContractBaseInfoLibMapper contractBaseInfoLibMapper;

    @Override
    public Long maxSingleContractClientApplyCreditAmountBefore(LocalDate snapshotDate) {
        Map<Long, List<ContractBaseInfoLib>> contractLibsByClientId = contractBaseInfoLibMapper
                .listNewestContractByPreviewIds(null)
                .stream()
                .filter(contract -> contract.getDataCreateTime().isBefore(snapshotDate.atTime(23, 59, 59)))
                .collect(Collectors.groupingBy(ContractBaseInfoLib::getClientId));
        Long maxApplyCreditAmount = 0L;
        for (Map.Entry<Long, List<ContractBaseInfoLib>> entry : contractLibsByClientId.entrySet()) {
            if (entry.getValue().size() != 1) {
                continue;
            }
            Long thisApplyCreditAmount = LongUtil.null2zero(entry.getValue().get(0).getApplyCreditAmount());
            maxApplyCreditAmount = Math.max(maxApplyCreditAmount, thisApplyCreditAmount);
        }
        return maxApplyCreditAmount;
    }
}
