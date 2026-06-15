package cn.zswltech.mithras.application.orchestration.adapter.filingmaterials;

import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.filingmaterials.application.port.FilingMaterialsContractInfoPort;
import cn.zswltech.mithras.filingmaterials.application.port.model.FilingMaterialsContractInfo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Component
public class FilingMaterialsContractInfoPortAdapter implements FilingMaterialsContractInfoPort {

    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    @Override
    public FilingMaterialsContractInfo getById(Long contractId) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        if (Objects.isNull(contractBaseInfo)) {
            return null;
        }
        FilingMaterialsContractInfo contractInfo = new FilingMaterialsContractInfo();
        contractInfo.setContractCode(contractBaseInfo.getContractCode());
        contractInfo.setProjName(contractBaseInfo.getProjName());
        contractInfo.setProjSponsorUserId(contractBaseInfo.getProjSponsorUserId());
        return contractInfo;
    }
}
