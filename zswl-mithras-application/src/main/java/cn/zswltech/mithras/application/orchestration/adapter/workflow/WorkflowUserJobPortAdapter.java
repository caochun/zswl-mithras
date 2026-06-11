package cn.zswltech.mithras.application.orchestration.adapter.workflow;

import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.workflow.process.prepare.WorkflowUserJobPort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class WorkflowUserJobPortAdapter implements WorkflowUserJobPort {

    @Resource
    private SysUserService sysUserService;

    @Override
    public Long getUserIdByOrgJob(Long orgId, String job) {
        return sysUserService.getUserIdByOrgJob(orgId, job);
    }

    @Override
    public List<String> queryUserJobList(Long userId) {
        return sysUserService.queryUserJobList(userId);
    }
}
