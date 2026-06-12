package cn.zswltech.mithras.policy.application.port;

import cn.zswltech.mithras.policy.application.port.model.PolicyContractInfo;

public interface PolicyContractInfoPort {

    PolicyContractInfo getById(Long contractId);
}
