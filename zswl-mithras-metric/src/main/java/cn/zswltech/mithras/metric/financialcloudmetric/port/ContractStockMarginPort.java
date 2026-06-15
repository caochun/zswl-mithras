package cn.zswltech.mithras.metric.financialcloudmetric.port;

import java.util.Map;
import java.util.Set;

public interface ContractStockMarginPort {

    Map<Long, Long> getCollectionAmountByContractIds(Set<Long> contractIds);
}
