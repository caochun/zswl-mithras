package cn.zswltech.mithras.contract.versioning.service;

import java.util.Map;
import java.util.Set;

public interface ContractPriceAmountResolver {

    Map<Long, Long> queryNewestContractAmount(Set<Long> contractIds);
}
