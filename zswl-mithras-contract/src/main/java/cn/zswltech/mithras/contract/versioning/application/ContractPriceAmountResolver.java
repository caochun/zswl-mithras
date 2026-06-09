package cn.zswltech.mithras.contract.versioning.application;

import java.util.Map;
import java.util.Set;

public interface ContractPriceAmountResolver {

    Map<Long, Long> queryNewestContractAmount(Set<Long> contractIds);
}
