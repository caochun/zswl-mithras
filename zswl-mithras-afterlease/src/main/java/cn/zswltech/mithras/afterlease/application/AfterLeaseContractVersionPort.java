package cn.zswltech.mithras.afterlease.application;

import java.util.Collection;
import java.util.List;

public interface AfterLeaseContractVersionPort {

    List<AfterLeaseContractVersionSnapshot> listLatestByContractIds(Collection<Long> contractIds);

    List<AfterLeaseContractRentVersionSnapshot> listNormalRentVersions(Collection<AfterLeaseContractVersionSnapshot> contractVersions);
}
