package cn.zswltech.mithras.filingmaterials.application.port;

import cn.zswltech.mithras.filingmaterials.application.port.model.FilingMaterialsContractInfo;

public interface FilingMaterialsContractInfoPort {

    FilingMaterialsContractInfo getById(Long contractId);
}
