package cn.zswltech.mithras.financeprojectdistribution.service;

import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;

import java.util.Collection;
import java.util.List;

public interface FinanceProjectDistributionSupportPort {

    List<UserDO> listSpecificOrgJobUser(Long deptId, String jobCode);

    OrgDO getBizDeptByUserId(Long userId);

    List<Long> jobUsers(Collection<String> jobCodes);

    void markProcessPrepareCommitted(Long businessId, String processType);

    void cc(ExecutionProcessBaseREQ req);
}
