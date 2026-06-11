package cn.zswltech.mithras.contract.overdue.application.collection;

import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.dto.persistence.OcContractListDto;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CollectionContractQueryService {

    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ContractRemainingPrincipalResolver contractRemainingPrincipalResolver;
    @Resource
    private ContractDepositBalanceResolver contractDepositBalanceResolver;

    public Map<Long, String> contractPulldown(Long clientId) {
        List<ContractBaseInfo> targetContracts = contractBaseInfoMapper.listByClientIds(clientId).stream()
                .filter(contract -> ContractStatus.START_RENT.name().equals(contract.getContractStatus()))
                .collect(Collectors.toList());
        return targetContracts.stream().collect(Collectors.toMap(ContractBaseInfo::getId, ContractBaseInfo::getContractCode));
    }

    public List<OcContractListDto> ocContractList(Long clientId) {
        List<OcContractListDto> ocContractListDtos = contractBaseInfoMapper.ocContractList(clientId);
        Map<Long, OcContractListDto> ocContractMap = ocContractListDtos.stream()
                .collect(Collectors.toMap(OcContractListDto::getId, item -> item));
        Map<Long, Long> remainingPrincipals =
                contractRemainingPrincipalResolver.remainingUnpaidPrincipalByContract(new ArrayList<>(ocContractMap.keySet()));
        Map<Long, Long> depositBalances = contractDepositBalanceResolver.depositBalances(ocContractMap.keySet());
        ocContractMap.forEach((id, item) -> {
            Long remainingPrincipal = remainingPrincipals.getOrDefault(id, 0L);
            Long depositBalance = depositBalances.getOrDefault(id, 0L);
            item.setRemainPrincipal(remainingPrincipal);
            item.setRemainDeposit(depositBalance);
            item.setRiskExposure(remainingPrincipal - depositBalance);
        });
        return new ArrayList<>(ocContractMap.values());
    }
}
