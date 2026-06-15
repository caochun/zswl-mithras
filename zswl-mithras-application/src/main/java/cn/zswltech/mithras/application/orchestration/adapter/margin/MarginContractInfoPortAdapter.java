package cn.zswltech.mithras.application.orchestration.adapter.margin;

import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractBaseInfoLibHandler;
import cn.zswltech.mithras.margin.application.port.MarginContractInfoPort;
import cn.zswltech.mithras.margin.application.port.model.MarginContractInfo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MarginContractInfoPortAdapter implements MarginContractInfoPort {
    @Resource
    private ContractBaseInfoLibHandler contractBaseInfoLibHandler;

    @Override
    public MarginContractInfo getLatestContractInfo(Long contractId) {
        ContractBaseInfoLib contract = contractBaseInfoLibHandler.queryLatestDataByOriginId(contractId);
        if (contract == null) {
            return null;
        }
        MarginContractInfo info = new MarginContractInfo();
        info.setClientId(contract.getClientId());
        info.setProjSponsorUserId(contract.getProjSponsorUserId());
        info.setBizDeptId(contract.getBizDeptId());
        info.setContractCode(contract.getContractCode());
        info.setBizType(contract.getBizType());
        info.setLeaseType(contract.getLeaseType());
        info.setProjName(contract.getProjName());
        ContractStatus contractStatus = ContractStatus.of(contract.getContractStatus());
        info.setContractStatus(contractStatus == null ? contract.getContractStatus() : contractStatus.display);
        return info;
    }
}
