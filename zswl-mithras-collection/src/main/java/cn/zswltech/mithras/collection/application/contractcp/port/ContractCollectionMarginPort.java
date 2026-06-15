package cn.zswltech.mithras.collection.application.contractcp.port;

import java.util.Collection;
import java.util.List;

public interface ContractCollectionMarginPort {

    ContractCollectionMarginInfo getByContractId(Long contractId);

    List<ContractCollectionMarginInfo> listByContractId(Long contractId);

    List<ContractCollectionMarginInfo> listByMarginCodes(Collection<String> marginCodes);

    List<ContractCollectionMarginInfo> listByContractIds(Collection<Long> contractIds);
}
