package cn.zswltech.mithras.afterlease.application;

import cn.zswltech.mithras.contract.model.contract.ContractRentActual;

import java.util.Collection;
import java.util.List;

public interface AfterLeaseContractRentActualPort {
    List<ContractRentActual> listByContractIds(Collection<Long> contractIds);
}
