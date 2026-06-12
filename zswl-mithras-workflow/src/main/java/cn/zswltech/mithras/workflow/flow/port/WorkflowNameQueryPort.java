package cn.zswltech.mithras.workflow.flow.port;

import java.util.Collection;
import java.util.Map;

public interface WorkflowNameQueryPort {

    Map<Long, String> sysUserId2Name(Collection<Long> userIds);

    Map<Long, String> deptId2Name(Collection<Long> deptIds);

    Map<Long, String> clientId2Name(Collection<Long> clientIds);

    Map<Long, Long> clientId2DeptId(Collection<Long> clientIds);
}
