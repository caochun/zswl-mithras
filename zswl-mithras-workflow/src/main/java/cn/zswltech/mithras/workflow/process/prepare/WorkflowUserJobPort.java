package cn.zswltech.mithras.workflow.process.prepare;

import java.util.List;

public interface WorkflowUserJobPort {

    Long getUserIdByOrgJob(Long orgId, String job);

    List<String> queryUserJobList(Long userId);
}
