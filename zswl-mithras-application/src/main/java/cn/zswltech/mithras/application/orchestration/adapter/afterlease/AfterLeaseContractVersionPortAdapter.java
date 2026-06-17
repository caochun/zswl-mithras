package cn.zswltech.mithras.application.orchestration.adapter.afterlease;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.afterlease.application.AfterLeaseContractRentVersionSnapshot;
import cn.zswltech.mithras.afterlease.application.AfterLeaseContractVersionPort;
import cn.zswltech.mithras.afterlease.application.AfterLeaseContractVersionSnapshot;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.contract.model.contract.ContractRentActualLib;
import cn.zswltech.mithras.contract.versioning.service.ContractBaseInfoLibService;
import cn.zswltech.mithras.contract.versioning.service.ContractRentActualLibService;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AfterLeaseContractVersionPortAdapter implements AfterLeaseContractVersionPort {

    @Resource
    private ContractBaseInfoLibService contractBaseInfoLibService;
    @Resource
    private ContractRentActualLibService contractRentActualLibService;

    @Override
    public List<AfterLeaseContractVersionSnapshot> listLatestByContractIds(Collection<Long> contractIds) {
        if (CollectionUtil.isEmpty(contractIds)) {
            return new LinkedList<>();
        }
        List<ContractBaseInfoLib> contractBaseInfoLibList = contractBaseInfoLibService.listByContractIds(contractIds);
        Map<Long, List<ContractBaseInfoLib>> contractBaseInfoLibMap = contractBaseInfoLibList.stream()
                .collect(Collectors.groupingBy(ContractBaseInfoLib::getOriginId));
        List<AfterLeaseContractVersionSnapshot> latestVersionContractList = new LinkedList<>();
        for (Map.Entry<Long, List<ContractBaseInfoLib>> map : contractBaseInfoLibMap.entrySet()) {
            List<ContractBaseInfoLib> list = map.getValue();
            list.sort(Comparator.comparing(ContractBaseInfoLib::getId));
            latestVersionContractList.add(toSnapshot(list.get(list.size() - 1)));
        }
        return latestVersionContractList;
    }

    @Override
    public List<AfterLeaseContractRentVersionSnapshot> listNormalRentVersions(Collection<AfterLeaseContractVersionSnapshot> contractVersions) {
        if (CollectionUtil.isEmpty(contractVersions)) {
            return new LinkedList<>();
        }
        LambdaQueryWrapper<ContractRentActualLib> queryRentLib = Wrappers.lambdaQuery();
        for (AfterLeaseContractVersionSnapshot contractVersion : contractVersions) {
            queryRentLib.or(true, innerQuery -> {
                innerQuery.eq(ContractRentActualLib::getContractId, contractVersion.getContractId());
                innerQuery.eq(ContractRentActualLib::getVersion, contractVersion.getVersion());
                innerQuery.eq(ContractRentActualLib::getVersionType, VersionTypeConstants.NORMAL);
            });
        }
        return contractRentActualLibService.list(queryRentLib)
                .stream()
                .map(this::toSnapshot)
                .collect(Collectors.toList());
    }

    private AfterLeaseContractVersionSnapshot toSnapshot(ContractBaseInfoLib contractBaseInfoLib) {
        AfterLeaseContractVersionSnapshot snapshot = new AfterLeaseContractVersionSnapshot();
        snapshot.setId(contractBaseInfoLib.getId());
        snapshot.setContractId(contractBaseInfoLib.getOriginId());
        snapshot.setVersion(contractBaseInfoLib.getVersion());
        snapshot.setApplyCreditAmount(contractBaseInfoLib.getApplyCreditAmount());
        return snapshot;
    }

    private AfterLeaseContractRentVersionSnapshot toSnapshot(ContractRentActualLib contractRentActualLib) {
        AfterLeaseContractRentVersionSnapshot snapshot = new AfterLeaseContractRentVersionSnapshot();
        snapshot.setContractId(contractRentActualLib.getContractId());
        snapshot.setVersion(contractRentActualLib.getVersion());
        snapshot.setCashFlowDate(contractRentActualLib.getCashFlowDate());
        snapshot.setRent(contractRentActualLib.getRent());
        return snapshot;
    }
}
