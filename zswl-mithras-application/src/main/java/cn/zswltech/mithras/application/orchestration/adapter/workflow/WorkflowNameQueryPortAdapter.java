package cn.zswltech.mithras.application.orchestration.adapter.workflow;

import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.workflow.flow.port.WorkflowNameQueryPort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Map;

@Component
public class WorkflowNameQueryPortAdapter implements WorkflowNameQueryPort {

    @Resource
    private Id2NameService id2NameService;

    @Override
    public Map<Long, String> sysUserId2Name(Collection<Long> userIds) {
        return id2NameService.sysUserId2Name(userIds);
    }

    @Override
    public Map<Long, String> deptId2Name(Collection<Long> deptIds) {
        return id2NameService.deptId2Name(deptIds);
    }

    @Override
    public Map<Long, String> clientId2Name(Collection<Long> clientIds) {
        return id2NameService.clientId2Name(clientIds);
    }

    @Override
    public Map<Long, Long> clientId2DeptId(Collection<Long> clientIds) {
        return id2NameService.clientId2DeptId(clientIds);
    }
}
