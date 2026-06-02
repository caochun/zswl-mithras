package cn.zswltech.mithras.contract.overdue.application.collection;

import java.util.List;
import java.util.Map;

public interface ContractRemainingPrincipalResolver {

    Map<Long, Long> remainingUnpaidPrincipalByContract(List<Long> contractIds);
}
