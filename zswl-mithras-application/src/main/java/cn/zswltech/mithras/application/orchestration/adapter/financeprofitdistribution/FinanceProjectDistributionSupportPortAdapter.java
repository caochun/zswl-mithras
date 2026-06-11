package cn.zswltech.mithras.application.orchestration.adapter.financeprofitdistribution;

import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;
import cn.zswltech.mithras.financeprojectdistribution.service.FinanceProjectDistributionSupportPort;
import cn.zswltech.mithras.application.orchestration.workflow.flow.service.ExecutionService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.workflow.enums.CommonProcessPrepareStatus;
import cn.zswltech.mithras.workflow.model.CommonProcessPrepare;
import cn.zswltech.mithras.workflow.mapper.CommonProcessPrepareMapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Component
public class FinanceProjectDistributionSupportPortAdapter implements FinanceProjectDistributionSupportPort {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private CommonProcessPrepareMapper commonProcessPrepareMapper;
    @Resource
    private ExecutionService executionService;

    @Override
    public List<UserDO> listSpecificOrgJobUser(Long deptId, String jobCode) {
        return sysUserService.listSpecificOrgJobUser(deptId, jobCode);
    }

    @Override
    public OrgDO getBizDeptByUserId(Long userId) {
        return sysUserService.getBizDeptByUserId(userId);
    }

    @Override
    public List<Long> jobUsers(Collection<String> jobCodes) {
        return sysUserService.jobUsers(new HashSet<>(jobCodes));
    }

    @Override
    public void markProcessPrepareCommitted(Long businessId, String processType) {
        LambdaUpdateWrapper<CommonProcessPrepare> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(CommonProcessPrepare::getStatus, CommonProcessPrepareStatus.COMMITTED.name());
        updateWrapper.eq(CommonProcessPrepare::getBusinessId, businessId);
        updateWrapper.eq(CommonProcessPrepare::getProcessType, processType);
        commonProcessPrepareMapper.update(null, updateWrapper);
    }

    @Override
    public void cc(ExecutionProcessBaseREQ req) {
        executionService.cc(req);
    }
}
