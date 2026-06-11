package cn.zswltech.mithras.foundation.port;

import cn.zswltech.mithras.dto.contract.ContractInfo;

import java.util.Collection;
import java.util.Map;

public interface ContractInfoResolver {

    Map<Long, ContractInfo> contractId2Info(Collection<Long> ids);
}
