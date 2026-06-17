package cn.zswltech.mithras.application.orchestration.adapter.collection;

import cn.zswltech.mithras.collection.application.CollectionDetailContractInfo;
import cn.zswltech.mithras.collection.application.CollectionDetailContractInfoPort;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractBaseInfoLibHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Component
public class CollectionDetailContractInfoPortAdapter implements CollectionDetailContractInfoPort {

    @Resource
    private ContractBaseInfoLibHandler contractBaseInfoLibHandler;

    @Override
    public CollectionDetailContractInfo getLatestContractInfo(Long contractId) {
        ContractBaseInfoLib contract = contractBaseInfoLibHandler.queryLatestDataByOriginId(contractId);
        if (contract == null) {
            return null;
        }
        CollectionDetailContractInfo info = new CollectionDetailContractInfo();
        info.setClientId(contract.getClientId());
        info.setContractCode(contract.getContractCode());
        info.setBizDeptId(contract.getBizDeptId());
        info.setProjName(contract.getProjName());
        info.setProjSponsorUserId(contract.getProjSponsorUserId());
        info.setBizType(contract.getBizType());
        info.setLeaseType(contract.getLeaseType());
        info.setContractStatusDisplay(Objects.requireNonNull(ContractStatus.of(contract.getContractStatus())).display);
        return info;
    }
}
