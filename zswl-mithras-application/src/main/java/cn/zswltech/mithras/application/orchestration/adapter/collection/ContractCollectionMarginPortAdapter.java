package cn.zswltech.mithras.application.orchestration.adapter.collection;

import cn.zswltech.mithras.collection.application.contractcp.port.ContractCollectionMarginInfo;
import cn.zswltech.mithras.collection.application.contractcp.port.ContractCollectionMarginPort;
import cn.zswltech.mithras.margin.application.port.model.MarginCollectionSnapshot;
import cn.zswltech.mithras.margin.service.MarginBaseInfoService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContractCollectionMarginPortAdapter implements ContractCollectionMarginPort {

    @Resource
    private MarginBaseInfoService marginBaseInfoService;

    @Override
    public ContractCollectionMarginInfo getByContractId(Long contractId) {
        return toInfo(marginBaseInfoService.getCollectionSnapshotByContractId(contractId));
    }

    @Override
    public List<ContractCollectionMarginInfo> listByContractId(Long contractId) {
        return marginBaseInfoService.listCollectionSnapshotsByContractId(contractId).stream()
                .map(this::toInfo)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContractCollectionMarginInfo> listByMarginCodes(Collection<String> marginCodes) {
        return marginBaseInfoService.listCollectionSnapshotsByMarginCodes(marginCodes).stream()
                .map(this::toInfo)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContractCollectionMarginInfo> listByContractIds(Collection<Long> contractIds) {
        return marginBaseInfoService.listCollectionSnapshotsByContractIds(contractIds).stream()
                .map(this::toInfo)
                .collect(Collectors.toList());
    }

    private ContractCollectionMarginInfo toInfo(MarginCollectionSnapshot snapshot) {
        if (snapshot == null) {
            return null;
        }
        ContractCollectionMarginInfo info = new ContractCollectionMarginInfo();
        info.setId(snapshot.getId());
        info.setContractId(snapshot.getContractId());
        info.setMarginCode(snapshot.getMarginCode());
        info.setCollectionAmount(snapshot.getCollectionAmount());
        info.setPlanMarginAmount(snapshot.getPlanMarginAmount());
        info.setPlanMarginDate(snapshot.getPlanMarginDate());
        return info;
    }
}
