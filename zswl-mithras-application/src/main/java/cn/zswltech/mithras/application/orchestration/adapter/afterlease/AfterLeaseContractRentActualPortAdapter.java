package cn.zswltech.mithras.application.orchestration.adapter.afterlease;

import cn.zswltech.mithras.afterlease.application.AfterLeaseContractRentActualPort;
import cn.zswltech.mithras.afterlease.application.AfterLeaseContractRentSnapshot;
import cn.zswltech.mithras.contract.model.contract.ContractRentActual;
import cn.zswltech.mithras.contract.core.ContractRentActualService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AfterLeaseContractRentActualPortAdapter implements AfterLeaseContractRentActualPort {
    @Resource
    private ContractRentActualService contractRentActualService;

    @Override
    public List<AfterLeaseContractRentSnapshot> listByContractIds(Collection<Long> contractIds) {
        return contractRentActualService.listByContractIds(contractIds)
                .stream()
                .map(this::toSnapshot)
                .collect(Collectors.toList());
    }

    private AfterLeaseContractRentSnapshot toSnapshot(ContractRentActual actual) {
        AfterLeaseContractRentSnapshot snapshot = new AfterLeaseContractRentSnapshot();
        snapshot.setContractId(actual.getContractId());
        snapshot.setCashFlowPhase(actual.getCashFlowPhase());
        snapshot.setCashFlowDate(actual.getCashFlowDate());
        return snapshot;
    }
}
