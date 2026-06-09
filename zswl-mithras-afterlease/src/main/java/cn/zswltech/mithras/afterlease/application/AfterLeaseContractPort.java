package cn.zswltech.mithras.afterlease.application;

import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;

import java.util.List;
import java.util.Set;

public interface AfterLeaseContractPort {
    List<ContractBaseInfo> listAllStartRent();

    List<ContractBaseInfo> listActiveByClientId(Long clientId);

    List<ContractBaseInfo> listInRentContract(Long clientId);

    ContractBaseInfo getById(Long contractId);

    void updateById(ContractBaseInfo contractBaseInfo);

    Long getStockRiskExposure(Long clientId);
}
