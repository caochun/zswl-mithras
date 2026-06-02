package cn.zswltech.mithras.contract.overdue.application.collection;

import java.util.Map;
import java.util.Set;

public interface ContractDepositBalanceResolver {

    Map<Long, Long> depositBalances(Set<Long> contractIds);
}
