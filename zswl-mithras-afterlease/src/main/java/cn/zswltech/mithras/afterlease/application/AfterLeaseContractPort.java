package cn.zswltech.mithras.afterlease.application;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AfterLeaseContractPort {
    List<AfterLeaseContractSnapshot> listAllStartRent();

    List<AfterLeaseContractSnapshot> listActiveByClientId(Long clientId);

    List<AfterLeaseContractSnapshot> listInRentContract(Long clientId);

    AfterLeaseContractApprovalContext getApprovalContextById(Long contractId);

    void markOverdueCollectionNotified(Long contractId);

    Long getStockRiskExposure(Long clientId);

    Map<Long, Set<String>> clientRoleNamesByContractId(Long contractId);
}
