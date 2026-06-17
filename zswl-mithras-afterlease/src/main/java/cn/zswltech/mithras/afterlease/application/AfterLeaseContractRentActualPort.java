package cn.zswltech.mithras.afterlease.application;

import java.util.Collection;
import java.util.List;

public interface AfterLeaseContractRentActualPort {
    List<AfterLeaseContractRentSnapshot> listByContractIds(Collection<Long> contractIds);
}
