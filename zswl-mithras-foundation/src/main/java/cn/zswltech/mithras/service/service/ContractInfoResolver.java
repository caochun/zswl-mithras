package cn.zswltech.mithras.service.service;

import cn.zswltech.mithras.dto.contract.ContractInfo;

import java.util.Collection;
import java.util.Map;

public interface ContractInfoResolver {

    Map<Long, ContractInfo> contractId2Info(Collection<Long> ids);
}
