package cn.zswltech.mithras.application.orchestration.adapter.leaseholdproperty;

import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.leaseholdproperty.application.port.LeaseholdContractContextPort;
import cn.zswltech.mithras.leaseholdproperty.application.port.LeaseholdContractContextSnapshot;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class LeaseholdContractContextPortAdapter implements LeaseholdContractContextPort {

    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    @Override
    public LeaseholdContractContextSnapshot getByContractId(Long contractId) {
        ContractBaseInfo contract = contractBaseInfoService.getById(contractId);
        if (contract == null) {
            return null;
        }
        return new LeaseholdContractContextSnapshot(contract.getId(), contract.getProjReviewId(), contract.getProjName(),
                contract.getClientId(), contract.getProjSponsorUserId(), contract.getProjCosponsorUserIds(),
                contract.getBizType(), contract.getLeaseType());
    }

    @Override
    public Integer getStockContractFlag(Long projReviewId) {
        return contractBaseInfoService.getStockContractFlag(projReviewId);
    }
}
