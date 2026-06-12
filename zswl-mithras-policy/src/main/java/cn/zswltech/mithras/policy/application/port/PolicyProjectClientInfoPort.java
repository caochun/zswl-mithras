package cn.zswltech.mithras.policy.application.port;

import cn.zswltech.mithras.policy.application.port.model.PolicyProjectClientInfo;

public interface PolicyProjectClientInfoPort {

    PolicyProjectClientInfo getByProjectId(Long projectId);
}
